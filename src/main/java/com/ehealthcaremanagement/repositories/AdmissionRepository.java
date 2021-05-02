package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.AdmissionModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AdmissionRepository extends JpaRepository<AdmissionModel, Long> {
    List<AdmissionModel> findAllByUserAndDoctorAndAdmitAfterAndDischargeBefore(
            UserModel userModel, DoctorModel doctorModel, LocalDate admit, LocalDate discharge
    );

    List<AdmissionModel> findAllByUserAndAdmitAfterAndDischargeBefore(
            UserModel userModel, LocalDate admit, LocalDate discharge
    );

    List<AdmissionModel> findAllByDoctorAndAdmitAfterAndDischargeBefore(
            DoctorModel doctorModel, LocalDate admit, LocalDate discharge
    );

    List<AdmissionModel> findAllByAdmitAfterAndDischargeBefore(LocalDate admit, LocalDate discharge);

    List<AdmissionModel> findAllByUserAndDoctorAndAdmitAfterAndDischargeIsNull(
            UserModel userModel, DoctorModel doctorModel, LocalDate admit
    );

    List<AdmissionModel> findAllByUserAndAdmitAfterAndDischargeIsNull(UserModel userModel, LocalDate admit);

    List<AdmissionModel> findAllByDoctorAndAdmitAfterAndDischargeIsNull(DoctorModel doctorModel, LocalDate admit);

    List<AdmissionModel> findAllByAdmitAfterAndDischargeIsNull(LocalDate admit);
}
