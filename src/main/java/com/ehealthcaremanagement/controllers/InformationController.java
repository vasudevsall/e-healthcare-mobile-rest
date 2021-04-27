package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.SpecialitiesModel;
import com.ehealthcaremanagement.services.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class InformationController {

    @Autowired
    private InformationService informationService;

    @RequestMapping(value = "/info/doctor", method = RequestMethod.GET)
    public @ResponseBody DoctorModel findDoctor(@RequestParam(name = "user") String username) {
        return informationService.getDoctorDetails(username);
    }

    @RequestMapping(value = "/info/doctor/speciality", method = RequestMethod.GET)
    public @ResponseBody List<DoctorModel> findAllDoctors(@RequestParam(name = "speciality")Optional<String> speciality) {
        if(speciality.isEmpty())
            return informationService.getAllDoctors();
        return informationService.getDoctorsBySpeciality(speciality.get());
    }

    @RequestMapping(value = "/info/speciality", method = RequestMethod.GET)
    public @ResponseBody List<SpecialitiesModel> findAllSpecialities() {
        return informationService.getAllSpecialities();
    }
}
