package com.ehealthcaremanagement.services.manager;

import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.services.FindModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ManagerUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FindModel findModel;

    public List<UserModel> getUserByUsername(String username) {
        List<UserModel> userModels = new ArrayList<>();
        userModels.add(findModel.findUserModel(username));
        return userModels;
    }

    public List<UserModel> getUserByPhone(String phone) {
        List<UserModel> userModels = userRepository.findAllByPhoneNumber(phone);
        if(userModels.size() <= 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with : " + phone);
        return userModels;
    }

    public List<UserModel> getUsersByEmail(String email) {
        List<UserModel> userModels = userRepository.findAllByEmail(email);
        if(userModels.size() <= 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with : " + email);
        return userModels;
    }
}
