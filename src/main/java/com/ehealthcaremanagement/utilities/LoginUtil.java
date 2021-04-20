package com.ehealthcaremanagement.utilities;

import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class LoginUtil {

    public static UserModel loginVerify(String username, UserRepository userRepository) throws UsernameNotFoundException {
        Optional<UserModel> userModel = userRepository.findByUsername(username);
        if(userModel.isPresent()) {
            return userModel.get();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

}
