package com.ehealthcaremanagement.controllers.manager;

import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.services.EmailSenderService;
import com.ehealthcaremanagement.services.PassGenerator;
import com.ehealthcaremanagement.services.manager.ManagerUserService;
import com.ehealthcaremanagement.utilities.RegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/manager")
@Controller
public class ManagePatientController {

    @Autowired
    private ManagerUserService managerUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSenderService emailService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public @ResponseBody List<UserModel> findUser(
            @RequestParam(name = "mode") Optional<String> mode,
            @RequestParam(name = "query") String query
    ) {
        if(mode.isEmpty() || mode.get().equals("username")) {
            return managerUserService.getUserByUsername(query);
        } else if(mode.get().equals("phone")) {
            return managerUserService.getUserByPhone(query);
        } else if (mode.get().equals("email")) {
            return managerUserService.getUsersByEmail(query);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode : " + mode.get());
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public @ResponseBody String addUser(@RequestBody UserModel user, HttpServletResponse response) {
        String password = PassGenerator.generatePassword();
        user.setPassword(password);
        RegistrationUtil registrationUtil = new RegistrationUtil(user, passwordEncoder, userRepository, null);
        registrationUtil.validateDetails();
        boolean status = registrationUtil.saveUser("ROLE_USER", true, true);
        try {
            emailService.sendWelcomeMail(user.getEmail(), user.getFirstName(), user.getUsername(),
                    password, false, true
            );
            return "Registration success";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to register at the momemt");
        }
    }
}
