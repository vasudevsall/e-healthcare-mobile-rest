package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.BlocksModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BlocksRepository extends JpaRepository<BlocksModel, Long> {
    public Optional<BlocksModel> findByDoctorModelAndDateAndSlot(DoctorModel doctorModel, LocalDate date, char slot);
    List<BlocksModel> findAllByDoctorModelAndDateAfter(DoctorModel doctorModel, LocalDate date);
}
