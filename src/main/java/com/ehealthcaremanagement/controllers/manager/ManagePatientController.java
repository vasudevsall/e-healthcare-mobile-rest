package com.ehealthcaremanagement.controllers.manager;

import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
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
        user.setPassword(user.getUsername() + user.getPhoneNumber().substring(0, 5) + "@");
        RegistrationUtil registrationUtil = new RegistrationUtil(user, passwordEncoder, userRepository, null);
        registrationUtil.validateDetails();
        boolean status = registrationUtil.saveUser("ROLE_USER", true, true);
        if(status) {
            return "Registration success";
        } else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Registration failed";
        }
        //TODO send messages to newly registered patient with username and password
    }
}
