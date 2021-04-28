package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.AppointmentDetailsModel;
import com.ehealthcaremanagement.models.repository.AppointmentModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentDetailsRepository extends JpaRepository<AppointmentDetailsModel, Long> {

    Optional<AppointmentDetailsModel> findByAppointmentModel(AppointmentModel appointmentModel);
    List<AppointmentDetailsModel> findAllByAppointmentModelInOrderByIdAsc(List<AppointmentModel> appointmentModels);
    List<AppointmentDetailsModel> findAllByUserModel(UserModel userModel);
}
