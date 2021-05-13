package com.ehealthcaremanagement.services;

import com.ehealthcaremanagement.models.custom.AppointmentDetailsUpdateModel;
import com.ehealthcaremanagement.models.repository.*;
import com.ehealthcaremanagement.repositories.AppointmentDetailsRepository;
import com.ehealthcaremanagement.repositories.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DoctorAppointmentService {

    @Autowired
    private FindModel findModel;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppointmentDetailsRepository appointmentDetailsRepository;

    private final Logger logger = LoggerFactory.getLogger(DoctorAppointmentService.class);

    public AppointmentModel nextPatient(Principal principal) {

        DoctorModel doctorModel = findModel.findDoctorModel(principal.getName());
        char slot = getSlot();

        List<AppointmentModel> appointmentModels = appointmentRepository.findAllByDoctorIdAndDateAndSlot(
                doctorModel, LocalDate.now(), slot
        );

        List<AppointmentDetailsModel> appointmentDetailsModels =
                appointmentDetailsRepository.findAllByAppointmentModelInOrderByIdAsc(appointmentModels);

        int lastToken = 0;
        if(appointmentDetailsModels.size() > 0) {
            lastToken = appointmentDetailsModels
                    .get(appointmentDetailsModels.size() - 1)
                    .getAppointmentModel()
                    .getToken();
        }

        BlocksModel blocksModel = findModel.findBlockModel(doctorModel, LocalDate.now(), slot);
        if(lastToken == blocksModel.getPatients()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No more patients");
        }

        return getNextAppointment(doctorModel, slot, lastToken);
    }

    public AppointmentDetailsModel updateAppointmentDetails(AppointmentDetailsUpdateModel appointmentDetailsUpdateModel) {
        UserModel userModel = findModel.findUserModel(appointmentDetailsUpdateModel.getUsername());
        AppointmentModel appointmentModel = findAppointmentById(appointmentDetailsUpdateModel.getAppointment());
        AppointmentDetailsModel appointmentDetailsModel = new AppointmentDetailsModel(
                userModel, appointmentModel,
                appointmentDetailsUpdateModel.getDiagnosis(),
                appointmentDetailsUpdateModel.getPrescription()
        );

        try {
            appointmentDetailsRepository.save(appointmentDetailsModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return appointmentDetailsModel;
    }

    /* Private methods */
    private char getSlot() {
        // Get the current time slot
        // If current time does not lie in any of the time slot then throw an error
        LocalDateTime currentTime = LocalDateTime.now();
        if(currentTime.isAfter(getLocalDateTime(8, 59)) && currentTime.isBefore(getLocalDateTime(13, 0))) {
            return 'M';
        } else if(
                currentTime.isAfter(getLocalDateTime(13, 59))
                        && currentTime.isBefore(getLocalDateTime(17, 30))
        ) {
            return 'A';
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid time to start appointment: " + currentTime.toString()
        );
    }

    private AppointmentModel getNextAppointment(DoctorModel doctorModel, char slot, int lastToken) {
        Optional<AppointmentModel> appointmentModelOptional =
                appointmentRepository.findByDoctorIdAndDateAndSlotAndToken(
                        doctorModel, LocalDate.now(), slot, lastToken + 1
                );

        if(appointmentModelOptional.isEmpty())
            return getNextAppointment(doctorModel, slot, lastToken+1);

        return appointmentModelOptional.get();
    }

    private LocalDateTime getLocalDateTime(int hours, int minutes) {
        LocalDate currentDate = LocalDate.now();
        LocalTime time = LocalTime.of(hours, minutes);
        return LocalDateTime.of(currentDate, time);
    }

    private AppointmentModel findAppointmentById(long id) {
        Optional<AppointmentModel> appointmentModelOptional = appointmentRepository.findById(id);
        if(appointmentModelOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Check Appointment id: " + id);
        }
        return appointmentModelOptional.get();
    }
}
