package com.ehealthcaremanagement.utilities;

import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserValidationUtil {

    private final UserRepository userRepository;
    private final List<Character> GENDERS = Arrays.asList('F', 'M', 'O', 'f', 'm', 'o');
    private final List<String> BLOOD_GROUPS = Arrays.asList("A+", "B+", "AB+", "O+", "A-", "B-", "AB-", "O-");

    public UserValidationUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void usernameValidation(String username) {
        Optional<UserModel> userModelOptional =  userRepository.findByUsername(username);

        if(userModelOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already used");
        }
        if(username.length() < 5 || username.length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username length must be between 5 to 20 characters");
        }
    }

    public void passwordValidation(String password) {
        if(password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters long");
        }
        if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&+=])(?=\\S+$).{6,}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must contain at lest one number and a special character and no spaces");
        }
    }

    public void phoneNumberValidation(String phone) {
        if(phone.length() != 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone Number must have 10 digits");
        }
        try {
            long phoneNum = Long.parseLong(phone);
        } catch (NumberFormatException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number can contain only digits");
        }
    }

    public void genderValidation(char gender) {
        if(!GENDERS.contains(gender)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender: " + gender + "\n choose from 'F', 'M' or 'O'");
        }
    }

    public void bloodGroupValidation(String blood) {
        if(!BLOOD_GROUPS.contains(blood)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Blood Group : " + blood);
        }
    }

    public void emailValidation(String email) {
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        if(!email.matches(regex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email id");
        }
    }

    public void nameValidation(String name) {
        String regex = "^[\\p{L} .'-]+$";
        if(!name.matches(regex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter a valid name");
        }
    }
}
