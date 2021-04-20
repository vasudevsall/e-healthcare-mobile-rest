package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.utilities.LoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/login-verify", method = RequestMethod.GET)
    public @ResponseBody UserModel verifyLogin(Principal principal, HttpServletResponse httpServletResponse) {
        try {
            return LoginUtil.loginVerify(principal.getName(), userRepository);
        } catch (Exception e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in", e);
        }
    }
}
