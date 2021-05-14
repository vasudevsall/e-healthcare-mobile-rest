package com.ehealthcaremanagement.utilities;

import com.ehealthcaremanagement.models.repository.RoomModel;
import com.ehealthcaremanagement.repositories.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class RoomUtil {

    private final RoomRepository roomRepository;
    private final RoomModel roomModel;

    private final Logger logger = LoggerFactory.getLogger(RoomUtil.class);

    public RoomUtil(RoomRepository roomRepository, RoomModel roomModel) {
        this.roomRepository = roomRepository;
        this.roomModel = roomModel;
    }

    public void validateRoomDetails(){
        Optional<RoomModel> roomModelOptional = roomRepository.findById(roomModel.getId());
        if(roomModelOptional.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room Number already exists: " + roomModel.getId());

        if(roomModel.getType() != 'W' || roomModel.getType() != 'I' || roomModel.getType() != 'O')
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid room type");

        if(roomModel.getBeds() > roomModel.getTotal())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total number of rooms cannot be less than available rooms");

        if(roomModel.getPrice() < 0.0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price : " + roomModel.getPrice() + "cannot be less than 0");
    }

    public RoomModel saveRoom() {
        try {
            return roomRepository.save(roomModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot register new room, please try again later");
        }
    }
}
