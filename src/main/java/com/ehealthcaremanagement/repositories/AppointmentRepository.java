package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.custom.PatientFrequencyModel;
import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {
    Optional<AppointmentModel> findByUserIdAndDoctorIdAndDateAndSlot(UserModel userModel, DoctorModel doctorModel, LocalDate date, char slot);
    Optional<AppointmentModel> findByDoctorIdAndDateAndSlotAndToken(DoctorModel doctorModel, LocalDate date, char slot, int token);
    List<AppointmentModel> findAllByDoctorIdAndDateAndSlot(DoctorModel doctorModel, LocalDate date, char slot);
    List<AppointmentModel> findAllByUserIdAndDateAndSlot(UserModel userModel, LocalDate date, char slot);
    List<AppointmentModel> findAllByUserIdAndDoctorIdAndDateAfter(UserModel userModel, DoctorModel doctorModel, LocalDate date);
    List<AppointmentModel> findAllByUserId(UserModel userModel);
    List<AppointmentModel> findAllByUserIdAndDateGreaterThanEqual(UserModel userModel, LocalDate date);
    List<AppointmentModel> findAllByUserIdAndDateBefore(UserModel userModel, LocalDate date);

    @Query("SELECT " +
            "new com.ehealthcaremanagement.models.custom.PatientFrequencyModel(COUNT(userId), userId)" +
            " FROM AppointmentModel WHERE doctorId=?1 AND date > ?2" +
            " GROUP BY userId ORDER BY COUNT(userId) DESC")
    List<PatientFrequencyModel> findRegularPatients(DoctorModel doctorModel, LocalDate date);
}
