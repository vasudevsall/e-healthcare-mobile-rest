package com.ehealthcaremanagement.utilities.appointments;

import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.BlocksModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.AppointmentRepository;
import com.ehealthcaremanagement.services.FindModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

public class AppointmentCancelUtil {

    private final AppointmentRepository appointmentRepository;
    private final long appointmentId;
    private final UserModel userModel;
    private final FindModel findModel;
    private AppointmentModel appointmentModel;

    private final Logger logger = LoggerFactory.getLogger(AppointmentCancelUtil.class);

    public AppointmentCancelUtil(AppointmentRepository appointmentRepository, long appointmentId, UserModel userModel, FindModel findModel) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentId = appointmentId;
        this.userModel = userModel;
        this.findModel = findModel;
    }

    public void findAppointmentById() {
        Optional<AppointmentModel> appointmentModelOptional =
                appointmentRepository.findById(appointmentId);

        if(appointmentModelOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment: " + appointmentId + " not found");
        }

        this.appointmentModel = appointmentModelOptional.get();
    }

    public void checkUserValid() {
        // Check if user has required permission to delete this appointment

        if(appointmentModel.getUserId().getId() != userModel.getId()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "You are not authorized to delete appointment: " + appointmentId
            );
        }
    }

    public void checkAppointmentDate() {
        boolean throwPastException = false;
        Period period = Period.between(LocalDate.now(), appointmentModel.getDate());
        if(period.isZero()) {
            if(appointmentModel.getSlot() == 'M' && LocalDateTime.now().getHour() > 12) {
                throwPastException = true;
            } else if(appointmentModel.getSlot() == 'A' && LocalDateTime.now().getHour() > 17) {
                throwPastException = true;
            }
        } else if(period.isNegative()) {
            throwPastException = true;
        }

        if(throwPastException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment: "+ appointmentId + " already over");
        }
    }

    public void reduceBlocks() {
        BlocksModel blocksModel =
                findModel.findBlockModel(appointmentModel.getDoctorId(), appointmentModel.getDate(), appointmentModel.getSlot());

        blocksModel.setBlocks(blocksModel.getBlocks() - appointmentModel.getBlocks());
        blocksModel.setCancelled(blocksModel.getCancelled() + 1);
        findModel.saveBlocksModel(blocksModel);
        // Number of patients not reduces due to use of patient number in First Come First Serve
    }

    public AppointmentModel deleteAppointment() {
        findAppointmentById();
        checkUserValid();
        checkAppointmentDate();
        reduceBlocks();

        appointmentRepository.delete(appointmentModel);

        return appointmentModel;
    }
}
