package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.custom.PasswordChangeModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.services.EmailSenderService;
import com.ehealthcaremanagement.utilities.RegistrationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSenderService emailService;

    private final Logger logger= LoggerFactory.getLogger(RegistrationController.class);

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody String newUser(@RequestBody UserModel user, HttpServletResponse response) {
        RegistrationUtil registrationUtil = new RegistrationUtil(user, passwordEncoder, userRepository, null);
        registrationUtil.validateDetails();
        boolean status = registrationUtil.saveUser("ROLE_USER", true, true);
        if(status) {
            try {
                emailService.sendWelcomeMail(user.getEmail(), user.getFirstName(), user.getUsername());
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
}
