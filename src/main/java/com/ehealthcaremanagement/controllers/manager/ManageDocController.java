package com.ehealthcaremanagement.controllers.manager;

import com.ehealthcaremanagement.models.custom.DoctorAnalysisModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.SpecialitiesModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.DoctorRepository;
import com.ehealthcaremanagement.repositories.SpecialityRepository;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.services.DoctorAnalysisService;
import com.ehealthcaremanagement.services.EmailSenderService;
import com.ehealthcaremanagement.services.FindModel;
import com.ehealthcaremanagement.services.PassGenerator;
import com.ehealthcaremanagement.utilities.RegistrationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

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
    @Autowired
    private DoctorAnalysisService doctorAnalysisService;
    @Autowired
    private EmailSenderService emailService;

    private final Logger logger = LoggerFactory.getLogger(ManageDocController.class);

    @RequestMapping(value = "/doctor", method = RequestMethod.POST)
    public @ResponseBody DoctorModel searchDoctorModel(@RequestBody DoctorModel doctorModel, HttpServletResponse response) {
        UserModel user = doctorModel.getUserId();
        String password = PassGenerator.generatePassword();
        user.setPassword(password);
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

                emailService.sendWelcomeMail(
                        user.getEmail(),
                        user.getFirstName() + " " + user.getLastName(),
                        user.getUsername(),
                        password,
                        true,
                        true
                );
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
    public @ResponseBody DoctorAnalysisModel getDoctorDetails(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "days") Optional<Integer> days
    )
    {
        if(days.isEmpty())
            return doctorAnalysisService.createDoctorAnalysis(username, 30);
        return doctorAnalysisService.createDoctorAnalysis(username, days.get());
    }

}
