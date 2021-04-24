package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.custom.AppointmentRequestModel;
import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.AppointmentRepository;
import com.ehealthcaremanagement.services.FindModel;
import com.ehealthcaremanagement.utilities.appointments.AppointmentUtil;
import com.ehealthcaremanagement.utilities.appointments.AppointmentValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

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
}
