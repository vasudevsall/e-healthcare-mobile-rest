package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {
    Optional<AppointmentModel> findByUserIdAndDoctorIdAndDateAndSlot(UserModel userModel, DoctorModel doctorModel, LocalDate date, char slot);
    List<AppointmentModel> findAllByDoctorIdAndDateAndSlot(DoctorModel doctorModel, LocalDate date, char slot);
    List<AppointmentModel> findAllByUserIdAndDateAndSlot(UserModel userModel, LocalDate date, char slot);
    List<AppointmentModel> findAllByUserIdAndDoctorIdAndDateAfter(UserModel userModel, DoctorModel doctorModel, LocalDate date);
    List<AppointmentModel> findAllByUserId(UserModel userModel);
    List<AppointmentModel> findAllByUserIdAndDateGreaterThanEqual(UserModel userModel, LocalDate date);
    List<AppointmentModel> findAllByUserIdAndDateBefore(UserModel userModel, LocalDate date);
}
