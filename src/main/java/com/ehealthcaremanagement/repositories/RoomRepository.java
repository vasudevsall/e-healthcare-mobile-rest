package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomModel, Long> {
    List<RoomModel> findAllByType(char type);

    @Query("SELECT " +
            "SUM(beds), SUM(total), type " +
            "FROM RoomModel group by type ")
    List<Object[]> findAvailableAndTotalRooms();
}
