package com.ehealthcaremanagement.utilities;

import com.ehealthcaremanagement.models.custom.PasswordChangeModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

public class RegistrationUtil {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserValidationUtil userValidation;
    private final Principal principal;
    private final PasswordChangeModel passwordChangeModel;

    private UserModel userModel;

    public RegistrationUtil(UserModel userModel, PasswordEncoder passwordEncoder, UserRepository userRepository, Principal principal) {
        this.userModel = userModel;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userValidation = new UserValidationUtil(userRepository);
        this.principal = principal;
        this.passwordChangeModel = null;
    }

    public RegistrationUtil(PasswordChangeModel passwordChangeModel, PasswordEncoder passwordEncoder, UserRepository userRepository, Principal principal) {
        this.passwordChangeModel = passwordChangeModel;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.principal = principal;
        this.userValidation = new UserValidationUtil(userRepository);
        this.userModel = null;
    }

    public void validateDetails() {
        userValidation.usernameValidation(userModel.getUsername()); // Username Validation
        userValidation.passwordValidation(userModel.getPassword()); // Password validation
        userValidation.nameValidation(userModel.getFirstName() + ' ' + userModel.getLastName()); // Name Validation
        userValidation.phoneNumberValidation(userModel.getPhoneNumber()); // Phone Number Validation
        userValidation.genderValidation(userModel.getGender()); // Gender validation
        userValidation.bloodGroupValidation(userModel.getBloodGroup()); // Blood Group Validation
        userValidation.emailValidation(userModel.getEmail()); // Email Validation
    }

    public UserModel getCurrentUser() {
        try{
            Optional<UserModel> userModelOptional = userRepository.findByUsername(principal.getName());
            if(userModelOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first!");
            }
            return userModelOptional.get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first!");
        }
    }

    public void verifyPassword(String password, String correctPassword) {
        if(password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your password");
        }
        if(!passwordEncoder.matches(password, correctPassword)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password! Please try again");
        }
    }

    public UserModel updateDetails() {
        UserModel original = getCurrentUser();
        verifyPassword(userModel.getPassword(), original.getPassword());

        // Password correct
        if(userModel.getFirstName() != null) {
            userValidation.nameValidation(userModel.getFirstName());
            original.setFirstName(userModel.getFirstName());
        }
        if(userModel.getLastName() != null) {
            userValidation.nameValidation(userModel.getLastName());
            original.setFirstName(userModel.getLastName());
        }
        if(userModel.getEmail() != null) {
            userValidation.emailValidation(userModel.getEmail());
            original.setEmail(userModel.getEmail());
        }
        if(userModel.getPhoneNumber() != null) {
            userValidation.phoneNumberValidation(userModel.getPhoneNumber());
            original.setPhoneNumber(userModel.getPhoneNumber());
        }
        if(userModel.getBirthDate() != null) {
            original.setBirthDate(userModel.getBirthDate());
        }
        if(userModel.getBloodGroup() != null) {
            userValidation.bloodGroupValidation(userModel.getBloodGroup());
            original.setBloodGroup(userModel.getBloodGroup());
        }

        this.userModel = original;
        saveUser("", false, false);
        return this.userModel;
    }

    public UserModel passwordChange() {
        this.userModel = getCurrentUser();
        verifyPassword(passwordChangeModel.getPassword(), userModel.getPassword());

        if(passwordChangeModel.getNewPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New Password empty");
        }
        if(passwordChangeModel.getRetype() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Retype new password!");
        }
        if(!passwordChangeModel.getNewPassword().equals(passwordChangeModel.getRetype())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords must match");
        }

        userValidation.passwordValidation(passwordChangeModel.getNewPassword());
        userValidation.passwordValidation(passwordChangeModel.getRetype());
        userModel.setPassword(passwordChangeModel.getNewPassword());
        saveUser("", false, true);

        return userModel;
    }

    public boolean saveUser(String roles, boolean registration, boolean passwordChange) {
        try {
            if(passwordChange) {
                String encodedPassword = passwordEncoder.encode(userModel.getPassword());
                userModel.setPassword(encodedPassword);
            }
            if(registration) {
                userModel.setActive(true);
                userModel.setRoles(roles);
            }
            userRepository.save(userModel);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
