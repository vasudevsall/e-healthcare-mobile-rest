package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorModel, Long> {
    Optional<DoctorModel> findByUserId(UserModel userModel);
}
