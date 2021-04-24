package com.ehealthcaremanagement.utilities.appointments;

import com.ehealthcaremanagement.models.custom.AppointmentRequestModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.services.FindModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class AppointmentValidationUtil {

    private final FindModel findModel;
    private final String userUsername;
    private final String doctorUsername;
    private final LocalDate date;
    private final char slot;

    public AppointmentValidationUtil(FindModel findModel, String userUsername, AppointmentRequestModel appointmentRequestModel) {
        this.findModel = findModel;
        this.userUsername = userUsername;
        this.doctorUsername = appointmentRequestModel.getDoctorUsername();
        this.date = appointmentRequestModel.getDate();
        this.slot = appointmentRequestModel.getSlot();
    }

    public boolean validateUser() {
        findModel.findUserModel(userUsername);
        return true;
    }

    public boolean validateDoctor() {
        findModel.findDoctorModel(doctorUsername);
        return true;
    }

    public boolean validateDate() {
        Period period = Period.between(LocalDate.now(), date);
        if(period.getDays() > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only one day advance scheduling permitted");
        }
        if(period.isNegative()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Past date entered");
        }
        return true;
    }

    public boolean validateSlot() {
        if(slot != 'M' && slot != 'A') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time slot: " + slot);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        Period period = Period.between(LocalDate.now(), date);
        if(slot == 'M') {
            if(period.isZero() && currentTime.getHour() > 7) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry, slot booking for this slot are closed," +
                        "\n Please visit healthcare facility for any emergencies");
            }
        }
        if(period.isZero() && currentTime.getHour() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry, slot booking for this slot are closed," +
                    "\n Please visit healthcare facility for any emergencies");
        }
        return true;
    }

    public boolean validateAll() {
        return validateUser() && validateDoctor() && validateDate() && validateSlot();
    }
}
