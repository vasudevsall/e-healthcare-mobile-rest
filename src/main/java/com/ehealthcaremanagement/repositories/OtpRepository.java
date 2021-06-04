package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.OtpModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpModel, Long> {
    Optional<OtpModel> findByUserAndTimeAfter(UserModel userModel, LocalDateTime time);
    Optional<OtpModel> findByUser(UserModel userModel);
}
