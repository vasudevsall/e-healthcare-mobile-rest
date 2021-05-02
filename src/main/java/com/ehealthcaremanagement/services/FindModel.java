package com.ehealthcaremanagement.services;

import com.ehealthcaremanagement.models.repository.*;
import com.ehealthcaremanagement.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class FindModel {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private BlocksRepository blocksRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private RoomRepository roomRepository;

    private final Logger logger = LoggerFactory.getLogger(FindModel.class);

    public UserModel findUserModel(String username) {
        Optional<UserModel> userModelOptional = userRepository.findByUsername(username);
        if(userModelOptional.isPresent()) {
            return userModelOptional.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " does not exist");
    }

    public DoctorModel findDoctorModel(String username) {
        UserModel user = findUserModel(username);
        Optional<DoctorModel> doctorModelOptional = doctorRepository.findByUserId(user);
        if(doctorModelOptional.isPresent()) {
            return doctorModelOptional.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor "+ username + "does not exist");
    }

    public BlocksModel findBlockModel(DoctorModel doctorModel, LocalDate date, char slot) {
        Optional<BlocksModel> blocksModelOptional =
                blocksRepository.findByDoctorModelAndDateAndSlot(doctorModel, date, slot);

        return blocksModelOptional.orElseGet(() -> new BlocksModel(doctorModel, date, slot, 0, 0, 0));
    }

    public BlocksModel findBlockModelOrCreate(DoctorModel doctorModel, LocalDate date, char slot) {
        BlocksModel blocksModel = findBlockModel(doctorModel, date, slot);
        saveBlocksModel(blocksModel);

        return blocksModel;
    }

    public boolean saveBlocksModel(BlocksModel blocksModel) {
        try {
            blocksRepository.save(blocksModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public SpecialitiesModel findSpecialitiesModel(String speciality){
        Optional<SpecialitiesModel> specialitiesModel = specialityRepository.findById(speciality);
        if(specialitiesModel.isPresent())
            return specialitiesModel.get();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No speciality found: " + speciality);
    }

    public RoomModel findRoomModel(long roomNo) {
        Optional<RoomModel> roomModelOptional = roomRepository.findById(roomNo);
        if(roomModelOptional.isPresent())
            return roomModelOptional.get();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Number " + roomNo + " not found");
    }
}
