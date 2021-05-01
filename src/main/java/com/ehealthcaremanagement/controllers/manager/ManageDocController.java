package com.ehealthcaremanagement.controllers.manager;

import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.SpecialitiesModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.DoctorRepository;
import com.ehealthcaremanagement.repositories.SpecialityRepository;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.services.FindModel;
import com.ehealthcaremanagement.utilities.RegistrationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/manager")
public class ManageDocController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FindModel findModel;
    @Autowired
    private SpecialityRepository specialityRepository;

    private final Logger logger = LoggerFactory.getLogger(ManageDocController.class);

    @RequestMapping(value = "/doctor", method = RequestMethod.POST)
    public @ResponseBody DoctorModel searchDoctorModel(@RequestBody DoctorModel doctorModel, HttpServletResponse response) {
        UserModel user = doctorModel.getUserId();
        user.setPassword(user.getUsername() + user.getPhoneNumber().substring(0, 5) + "@");
        RegistrationUtil registrationUtil = new RegistrationUtil(user, passwordEncoder, userRepository, null);
        registrationUtil.validateDetails();
        boolean status = registrationUtil.saveUser("ROLE_DOC", true, true);
        user = findModel.findUserModel(user.getUsername());
        if(status) {
            SpecialitiesModel specialitiesModel = doctorModel.getSpeciality();
            specialitiesModel = findModel.findSpecialitiesModel(specialitiesModel.getSpeciality());
            doctorModel.setUserId(user);
            doctorModel.setSpeciality(specialitiesModel);
            try {
                doctorRepository.save(doctorModel);
                specialitiesModel.setDoctorNumber(specialitiesModel.getDoctorNumber() + 1);
                specialityRepository.save(specialitiesModel);
                doctorModel = findModel.findDoctorModel(user.getUsername());
                return doctorModel;
            } catch (Exception e) {
                logger.error(e.getMessage());
                userRepository.delete(user);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot register doctor");
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot register doctor");
    }

    @RequestMapping(value = "/doctor", method = RequestMethod.GET)
    public @ResponseBody DoctorModel getDoctorDetails() {
        //TODO Just a method stub
        return new DoctorModel();
    }

}
