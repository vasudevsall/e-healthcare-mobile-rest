package com.ehealthcaremanagement.services;

import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.SpecialitiesModel;
import com.ehealthcaremanagement.repositories.DoctorRepository;
import com.ehealthcaremanagement.repositories.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class InformationService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private SpecialityRepository specialityRepository;
    @Autowired
    private FindModel findModel;

    public DoctorModel getDoctorDetails(String username) {
        return findModel.findDoctorModel(username);
    }

    public List<DoctorModel> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<DoctorModel> getDoctorsBySpeciality(String speciality) {
        Optional<SpecialitiesModel> specialitiesModelOptional = specialityRepository.findById(speciality);
        if(specialitiesModelOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Speciality name: " + speciality);
        }
        SpecialitiesModel specialitiesModel = specialitiesModelOptional.get();
        return doctorRepository.findAllBySpeciality(specialitiesModel);
    }

    public List<SpecialitiesModel> getAllSpecialities() {
        return specialityRepository.findAll();
    }
}
