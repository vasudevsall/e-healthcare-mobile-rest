package com.ehealthcaremanagement.scheduled;

import com.ehealthcaremanagement.models.custom.AppointmentComparator;
import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.repositories.AppointmentRepository;
import com.ehealthcaremanagement.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AppointmentScheduled {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Scheduled(cron = "0 30 5,10 * * *", zone = "IST")
    public void generateAppointmentToken() {
        // Shortest Job First scheduling algo, for minimum average waiting time
        char slot = (LocalDateTime.now().getHour() < 9) ? 'M' : 'A';
        List<DoctorModel> doctors = doctorRepository.findAll();
        for(DoctorModel doctor: doctors) {
            List<AppointmentModel> appointmentModels =
                    appointmentRepository.findAllByDoctorIdAndDateAndSlot(
                            doctor, LocalDate.now(), slot
                    );

            generateTokenNumbers(appointmentModels);
        }

        // TODO: send notification and messages for token number
    }

    @Async
    public void generateTokenNumbers(List<AppointmentModel> appointmentModels) {
        appointmentModels.sort(new AppointmentComparator());

        for(int index = 0; index<appointmentModels.size(); index++) {
            appointmentModels.get(index).setToken(index + 1);
            appointmentRepository.save(appointmentModels.get(index));
        }
    }
}
