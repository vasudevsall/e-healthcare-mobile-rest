package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomModel, Long> {
    List<RoomModel> findAllByType(char type);
}
