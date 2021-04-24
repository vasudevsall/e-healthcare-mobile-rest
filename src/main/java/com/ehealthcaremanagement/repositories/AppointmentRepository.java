package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {
    List<AppointmentModel> findAllByDoctorIdAndDateAndSlot(DoctorModel doctorModel, LocalDate date, char slot);
    List<AppointmentModel> findAllByUserIdAndDateAndSlot(UserModel userModel, LocalDate date, char slot);
    List<AppointmentModel> findAllByUserIdAndDoctorIdAndDateAfter(UserModel userModel, DoctorModel doctorModel, LocalDate date);
}
