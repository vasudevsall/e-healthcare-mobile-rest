package com.ehealthcaremanagement.controllers.doctors;

import com.ehealthcaremanagement.models.custom.AppointmentDetailsUpdateModel;
import com.ehealthcaremanagement.models.repository.*;
import com.ehealthcaremanagement.services.DoctorAppointmentService;
import com.ehealthcaremanagement.services.FindModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping(value = "/doctor")
public class DoctorController {

    @Autowired
    private DoctorAppointmentService doctorAppointmentService;
    @Autowired
    private FindModel findModel;

    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public @ResponseBody BlocksModel getNextSlotDetails(Principal principal) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DoctorModel doctorModel = findModel.findDoctorModel(principal.getName());
        if(localDateTime.getHour() <= 9) {
            return findModel.findBlockModel(doctorModel, LocalDate.now(), 'M');
        } else if (localDateTime.getHour() <= 14) {
            return findModel.findBlockModel(doctorModel, LocalDate.now(), 'A');
        }
        if(LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY)
            return findModel.findBlockModel(doctorModel, LocalDate.now().plusDays(2), 'M');
        return findModel.findBlockModel(doctorModel, LocalDate.now().plusDays(1), 'M');
    }

    @RequestMapping(value = "/patient", method = RequestMethod.GET)
    public @ResponseBody UserModel getPatientDetails(@RequestParam(name = "username") String username) {
        //TODO: Stub only generate complete details after accommodation
        return new UserModel();
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    public @ResponseBody AppointmentModel startAppointment(Principal principal) {
        return doctorAppointmentService.nextPatient(principal);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody AppointmentDetailsModel updateAppointmentDetails(
            @RequestBody AppointmentDetailsUpdateModel appointmentDetailsUpdateModel
    ) {
        return doctorAppointmentService.updateAppointmentDetails(appointmentDetailsUpdateModel);
    }
}
