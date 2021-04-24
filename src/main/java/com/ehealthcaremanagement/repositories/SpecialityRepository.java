package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.SpecialitiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialityRepository extends JpaRepository<SpecialitiesModel, String> {
}
