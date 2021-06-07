package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.custom.PasswordChangeModel;
import com.ehealthcaremanagement.models.repository.OtpModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.OtpRepository;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.services.EmailSenderService;
import com.ehealthcaremanagement.services.FindModel;
import com.ehealthcaremanagement.services.PassGenerator;
import com.ehealthcaremanagement.utilities.RegistrationUtil;
import com.ehealthcaremanagement.utilities.UserValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FindModel findModel;

    @Autowired
    private EmailSenderService emailService;

    @Autowired
    private OtpRepository otpRepository;

    private final Logger logger= LoggerFactory.getLogger(RegistrationController.class);

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody String newUser(@RequestBody UserModel user, HttpServletResponse response) {
        RegistrationUtil registrationUtil = new RegistrationUtil(user, passwordEncoder, userRepository, null);
        registrationUtil.validateDetails();
        boolean status = registrationUtil.saveUser("ROLE_USER", true, true);
        if(status) {
            try {
                emailService.sendWelcomeMail(
                        user.getEmail(), user.getFirstName(),
                        user.getUsername(),"", false, false
                );
            } catch (Exception e) {
                logger.error("Unable to send mail on registration username: " + user.getEmail());
                logger.error(e.getMessage());
            }
            return "Registration success";
        } else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Registration failed";
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.PUT)
    public @ResponseBody UserModel updateUser(@RequestBody UserModel user, Principal principal) {
        RegistrationUtil registrationUtil = new RegistrationUtil(user, passwordEncoder, userRepository, principal);
        return registrationUtil.updateDetails();
    }

    @RequestMapping(value = "/register/password", method = RequestMethod.PUT)
    public @ResponseBody UserModel updatePassword(@RequestBody PasswordChangeModel passwordChangeModel, Principal principal) {
        RegistrationUtil registrationUtil = new RegistrationUtil(passwordChangeModel, passwordEncoder, userRepository, principal);
        return registrationUtil.passwordChange();
    }

    @RequestMapping(value = "register/forgot", method = RequestMethod.GET)
    public @ResponseBody String forgotPasswordOtpGeneration(@RequestParam(name = "username") String username) {
        UserModel userModel = findModel.findUserModel(username);
        int otp = PassGenerator.generateOtp();
        Optional<OtpModel> otpModelOptional = otpRepository.findByUser(userModel);
        OtpModel otpModel;
        if(otpModelOptional.isEmpty())  otpModel = new OtpModel(userModel, otp, LocalDateTime.now());
        else {
            otpModel = otpModelOptional.get();
            otpModel.setPassword(otp);
            otpModel.setTime(LocalDateTime.now());
        }

        try {
            otpRepository.save(otpModel);
            emailService.sendOtpMail(userModel.getEmail(), userModel.getFirstName(), userModel.getUsername(), otp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Cannot generate OTP at the moment, please try again later");
        }
        return "OTP has been mailed to you, please check your mail.";
    }

    @RequestMapping(value = "/register/verify/otp", method = RequestMethod.POST)
    public @ResponseBody String verifyOtp(@RequestParam(name = "username") String username,
            @RequestBody long otp) {
        UserModel userModel = findModel.findUserModel(username);
        Optional<OtpModel> otpModelOptional =
                otpRepository.findByUserAndTimeAfter(userModel, LocalDateTime.now().minusMinutes(5));
        if(otpModelOptional.isPresent()) {
            OtpModel otpModel = otpModelOptional.get();

            if(otp == otpModel.getPassword()) {
                long newOtp = PassGenerator.generateOtp();
                otpModel.setPassword(newOtp);
                otpModel.setTime(LocalDateTime.now());

                otpRepository.save(otpModel);
                return Long.toString(newOtp);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP did not match");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please generate new OTP");
        }
    }

    @RequestMapping(value = "register/forgot", method = RequestMethod.POST)
    public @ResponseBody String forgotPassword(@RequestParam(name = "username") String username,
                                               @RequestBody PasswordChangeModel passwordChangeModel)
    {
        try {
            UserModel userModel = findModel.findUserModel(username); // Search user
            long otp = Long.parseLong(passwordChangeModel.getPassword()); // Get OTP
            Optional<OtpModel> otpModelOptional =
                    otpRepository.findByUserAndTimeAfter(userModel, LocalDateTime.now().minusMinutes(5)); // Search OTP in database
            if(otpModelOptional.isPresent()) {
                OtpModel otpModel = otpModelOptional.get();

                if(otp == otpModel.getPassword()) { // If otp matches

                    // validate password
                    UserValidationUtil userValidationUtil = new UserValidationUtil(userRepository);
                    userValidationUtil.passwordValidation(passwordChangeModel.getNewPassword());

                    // change password
                    userModel.setPassword(passwordEncoder.encode(passwordChangeModel.getNewPassword()));
                    userRepository.save(userModel);

                    // make time 1 hour earlier to invalidate otp
                    otpModel.setTime(LocalDateTime.now().minusHours(1));
                    otpRepository.save(otpModel);

                    emailService.sendPasswordChangeMail(userModel.getEmail(), userModel.getFirstName(), userModel.getUsername());

                } else { // If otp does not match
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please check OTP entered");
                }
            } else { // No otp in Database
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OTP expired for user: "+ username);
            }

        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        } catch (ResponseStatusException e) {
            throw  e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to verify now");
        }
        return "Password changed successfully!";
    }
}
