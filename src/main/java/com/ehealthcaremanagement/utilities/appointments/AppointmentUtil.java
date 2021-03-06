package com.ehealthcaremanagement.utilities.appointments;

import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.BlocksModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
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
import java.util.List;
import java.util.Optional;

public class AppointmentUtil {

    private final UserModel user;
    private final DoctorModel doctor;
    private final LocalDate date;
    private final char slot;
    private final char type;
    private final AppointmentRepository appointmentRepository;
    private final FindModel findModel;

    private int patientNumber;

    /* Number of blocks to be adjusted for different patients and doctor types */
    /* All the constants here are for general physicians, for specialized doctors, double may be considered */
    private static final double FEMALE_DOC = 0.5;
    private static final double SENIOR_CITIZEN = 1;
    private static final double EXPERIENCE = -1;
    private static final double NEW_PATIENT = 0.5;

    private static final String GENERAL_PHYSICIAN = "General Physician";

    private static final Logger log = LoggerFactory.getLogger(AppointmentUtil.class);

    public AppointmentUtil(UserModel user, DoctorModel doctor, LocalDate date,
                           char slot, char type, AppointmentRepository appointmentRepository,
                           FindModel findModel)
    {
        this.user = user;
        this.doctor = doctor;
        this.date = date;
        this.slot = slot;
        this.type = type;
        this.appointmentRepository = appointmentRepository;
        this.findModel = findModel;
    }

    public boolean isNewPatient() {
        LocalDate date2WeeksAgo = LocalDate.now().minusDays(14);
        List<AppointmentModel> appointmentModelList =
                appointmentRepository.findAllByUserIdAndDoctorIdAndDateAfter(user, doctor, date2WeeksAgo);

        return appointmentModelList.size() == 0;
    }

    public int getUserAge() {
        Period period  = Period.between(user.getBirthDate(), LocalDate.now());
        return period.getYears();
    }

    public double calculateBlocks() {
        double initialBlocks = doctor.getSpeciality().getTime();
        double change = 0.0;

        if(getUserAge() > 50 ) {
            change += SENIOR_CITIZEN;
        } else if(doctor.getUserId().getGender() == 'F') {
            change += FEMALE_DOC;
        }
        if(doctor.getExperience() > 8) {
            change += EXPERIENCE;
        }
        if(isNewPatient()) {
            change += NEW_PATIENT;
        }

        if(!doctor.getSpeciality().getSpeciality().equals(GENERAL_PHYSICIAN)) {
            return initialBlocks + change * 2;
        }
        return initialBlocks + change;
    }

    public void checkEmptyBlocks(double blocksRequired) {
        BlocksModel blocksModel = findModel.findBlockModelOrCreate(doctor, date, slot);
        double totalTimeBlocks = (slot == 'M') ? doctor.getMorningBlocks() : doctor.getAfternoonBlocks();

        this.patientNumber = blocksModel.getPatients() + 1;

        if((totalTimeBlocks - blocksModel.getBlocks()) < blocksRequired) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, " Sorry, slot already full");
        }
    }

    public void updateBlocks(double blocks) {
        BlocksModel blocksModel = findModel.findBlockModel(doctor, date, slot);
        double currentBlocks = blocksModel.getBlocks();
        int currentPatients = blocksModel.getPatients();
        blocksModel.setBlocks(currentBlocks + blocks);
        blocksModel.setPatients(currentPatients + 1);

        try {
            findModel.saveBlocksModel(blocksModel);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public void checkNoDuplicate() {
        Optional<AppointmentModel> appointmentModelOptional
                 = appointmentRepository.findByUserIdAndDoctorIdAndDateAndSlot(user, doctor, date, slot);

        if(appointmentModelOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate appointments not allowed");
        }
    }

    public void checkFCFSRequirement(AppointmentModel appointmentModel) {
        LocalDateTime currentTime = LocalDateTime.now();

        if(slot == 'M') {
            if(currentTime.getHour() > 5 && appointmentModel.getDate().equals(LocalDate.now())) {
                appointmentModel.setToken(this.patientNumber);
            }
        } else {
            if(currentTime.getHour() > 10 && appointmentModel.getDate().equals(LocalDate.now())) {
                appointmentModel.setToken(this.patientNumber);
            }
        }
    }

    public AppointmentModel saveAppointment() {
        double blocksRequired = calculateBlocks();
        checkEmptyBlocks(blocksRequired);
        checkNoDuplicate();

        AppointmentModel newAppointment = new AppointmentModel(
                user, doctor, date, slot, type, blocksRequired
        );

        checkFCFSRequirement(newAppointment);

        try {
            appointmentRepository.save(newAppointment);

            updateBlocks(blocksRequired);
            return newAppointment;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new AppointmentModel();
        }
    }
}