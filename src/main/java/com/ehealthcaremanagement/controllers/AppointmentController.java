package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.custom.AppointmentRequestModel;
import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.AppointmentRepository;
import com.ehealthcaremanagement.services.FindModel;
import com.ehealthcaremanagement.utilities.appointments.AppointmentCancelUtil;
import com.ehealthcaremanagement.utilities.appointments.AppointmentUtil;
import com.ehealthcaremanagement.utilities.appointments.AppointmentValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class AppointmentController {

    @Autowired
    private FindModel findModel;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @RequestMapping(value = "/appointment", method = RequestMethod.POST)
    public @ResponseBody AppointmentModel scheduleAppointment(@RequestBody AppointmentRequestModel appointmentRequestModel,
                                                              Principal principal)
    {
        UserModel userModel = findModel.findUserModel(principal.getName());
        DoctorModel doctorModel = findModel.findDoctorModel(appointmentRequestModel.getDoctorUsername());

        AppointmentValidationUtil appointmentValidationUtil = new AppointmentValidationUtil(
                findModel, principal.getName(), appointmentRequestModel
        );
        appointmentValidationUtil.validateAll();

        AppointmentUtil appointmentUtil = new AppointmentUtil(
                userModel, doctorModel, appointmentRequestModel.getDate(), appointmentRequestModel.getSlot(),
                appointmentRepository, findModel
        );

        return appointmentUtil.saveAppointment();
    }

    @RequestMapping(value = "/appointment", method = RequestMethod.GET)
    public @ResponseBody List<AppointmentModel> getAppointments(@RequestParam(name = "mode")Optional<Character> mode,
                                                                Principal principal)
            // Mode: null -> All, 'F' -> Future appointments, 'P' -> Past appointments
    {
        UserModel userModel = findModel.findUserModel(principal.getName());;
        if(mode.isEmpty()) {
            return appointmentRepository.findAllByUserId(userModel);
        } else {
            if(mode.get() == 'F')
                return appointmentRepository.findAllByUserIdAndDateGreaterThanEqual(userModel, LocalDate.now());
            return appointmentRepository.findAllByUserIdAndDateBefore(userModel, LocalDate.now());
        }
    }

    @RequestMapping(value = "/appointment", method = RequestMethod.DELETE)
    public @ResponseBody AppointmentModel cancelAppointment(@RequestParam(name = "id") long id, Principal principal) {
        AppointmentCancelUtil appointmentCancelUtil =
               new AppointmentCancelUtil(appointmentRepository, id, findModel.findUserModel(principal.getName()), findModel);

        return appointmentCancelUtil.deleteAppointment();
    }
}
