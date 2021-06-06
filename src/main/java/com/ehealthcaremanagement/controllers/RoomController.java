package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.repository.*;
import com.ehealthcaremanagement.repositories.AdmissionRepository;
import com.ehealthcaremanagement.repositories.RoomRepository;
import com.ehealthcaremanagement.services.FindModel;
import com.ehealthcaremanagement.utilities.RoomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/room")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private AdmissionRepository admissionRepository;
    @Autowired
    private FindModel findModel;

    private final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<RoomModel> getAllRooms(@RequestParam(name = "type") Optional<Character> type) {
        if(type.isEmpty())
            return roomRepository.findAll();
        else if(type.get() == 'W')
            return roomRepository.findAllByType('W');
        else if(type.get() == 'I')
            return roomRepository.findAllByType('I');
        else if(type.get() == 'O')
            return roomRepository.findAllByType('O');
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid room type provided");
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody RoomModel addNewRoom(@RequestBody RoomModel roomModel) {
        RoomUtil roomUtil = new RoomUtil(roomRepository, roomModel);
        roomUtil.validateRoomDetails();
        return roomUtil.saveRoom();
    }

    @RequestMapping(value = "/book", method = RequestMethod.GET)
    public @ResponseBody List<AdmissionModel> getRoomBookings(
            @RequestParam(name = "doctor") Optional<String> doctor,
            @RequestParam(name = "user") Optional<String> user,
            @RequestParam(name = "start")long startDays,
            @RequestParam(name = "end") Optional<Long> endDays
    ) {
        LocalDate startDate = LocalDate.now().minusDays(startDays);
        LocalDate endDate = endDays.map(aLong -> LocalDate.now().minusDays(aLong)).orElse(null);
        if(endDays.isEmpty())
            return getAdmissionModelsIfNull(doctor, user, startDate);
        return getAdmissionModels(doctor, user, startDate, endDate);
    }

    @RequestMapping(value = "/book", method = RequestMethod.POST)
    public @ResponseBody AdmissionModel admitPatient(@RequestBody AdmissionModel admissionModel) {
        RoomModel roomModel = findModel.findRoomModel(admissionModel.getRoom().getId());
        UserModel userModel = findModel.findUserModel(admissionModel.getUser().getUsername());
        DoctorModel doctorModel = findModel.findDoctorModel(admissionModel.getDoctor().getUserId().getUsername());

        admissionModel.setRoom(roomModel);
        admissionModel.setUser(userModel);
        admissionModel.setDoctor(doctorModel);
        admissionModel.setAdmit(LocalDate.now());

        if(roomModel.getBeds() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All beds occupied");

        roomModel.setBeds(roomModel.getBeds() - 1);

        try {
            roomRepository.save(roomModel);
            return admissionRepository.save(admissionModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot admit patient");
        }
    }

    @RequestMapping(value = "/book", method = RequestMethod.DELETE)
    public @ResponseBody AdmissionModel dischargePatient(@RequestParam(name = "id") long id) {
        Optional<AdmissionModel> admissionModelOptional = admissionRepository.findById(id);
        if(admissionModelOptional.isPresent()) {
            AdmissionModel admissionModel = admissionModelOptional.get();
            // Discharge the patient
            admissionModel.setDischarge(LocalDate.now());

            // Increase the number of available rooms
            RoomModel roomModel = admissionModel.getRoom();
            roomModel.setBeds(roomModel.getBeds() + 1);
            roomRepository.save(roomModel);

            // Calculate Bill and return details
            LocalDate admit = admissionModel.getAdmit();
            Period period = Period.between(admit, LocalDate.now());
            int days = period.getDays() + 1;
            admissionModel.setCost(admissionModel.getRoom().getPrice() * days);
            return admissionRepository.save(admissionModel);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient admitted with id: " + id + " not found");
    }

    // support functions
    private List<AdmissionModel> getAdmissionModels(
            Optional<String> doctor, Optional<String> user, LocalDate startDate, LocalDate endDate
    ) {
        if(doctor.isEmpty() && user.isEmpty()) {
            return admissionRepository.findAllByAdmitAfterAndDischargeBefore(startDate, endDate);
        } else if(doctor.isEmpty()) {
            UserModel userModel = findModel.findUserModel(user.get());
            return admissionRepository.findAllByUserAndAdmitAfterAndDischargeBefore(userModel, startDate, endDate);
        } else if(user.isEmpty()) {
            DoctorModel doctorModel = findModel.findDoctorModel(doctor.get());
            return admissionRepository.findAllByDoctorAndAdmitAfterAndDischargeBefore(doctorModel, startDate, endDate);
        }
        UserModel userModel = findModel.findUserModel(user.get());
        DoctorModel doctorModel = findModel.findDoctorModel(doctor.get());
        return admissionRepository.findAllByUserAndDoctorAndAdmitAfterAndDischargeBefore(
                userModel, doctorModel, startDate, endDate
        );
    }

    private List<AdmissionModel> getAdmissionModelsIfNull(
            Optional<String> doctor, Optional<String> user, LocalDate startDate
    ) {
        if(doctor.isEmpty() && user.isEmpty()) {
            return admissionRepository.findAllByAdmitAfterAndDischargeIsNull(startDate);
        } else if(doctor.isEmpty()) {
            UserModel userModel = findModel.findUserModel(user.get());
            return admissionRepository.findAllByUserAndAdmitAfterAndDischargeIsNull(userModel, startDate);
        } else if(user.isEmpty()) {
            DoctorModel doctorModel = findModel.findDoctorModel(doctor.get());
            return admissionRepository.findAllByDoctorAndAdmitAfterAndDischargeIsNull(doctorModel, startDate);
        }
        UserModel userModel = findModel.findUserModel(user.get());
        DoctorModel doctorModel = findModel.findDoctorModel(doctor.get());
        return admissionRepository.findAllByUserAndDoctorAndAdmitAfterAndDischargeIsNull(
                userModel, doctorModel, startDate
        );
    }
}
