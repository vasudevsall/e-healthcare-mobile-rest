package com.ehealthcaremanagement.controllers.manager;

import com.ehealthcaremanagement.repositories.AppointmentRepository;
import com.ehealthcaremanagement.repositories.RoomRepository;
import com.ehealthcaremanagement.repositories.UserRepository;
import com.ehealthcaremanagement.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ManagerAnalysisController {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailSenderService emailService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public @ResponseBody String testMethod() {
        try {
            emailService.sendWelcomeMail("ehealthcare8278338@gmail.com", "Vasudev", "vasudevsall");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send mail");
        }
        return "Mail Sent";
    }

    @RequestMapping(value = "/room/beds", method = RequestMethod.GET)
    public @ResponseBody Map<Character, Map<String, Long>> getRoomNumbers() {
        List<Object[]> objects = roomRepository.findAvailableAndTotalRooms();
        Map<Character, Map<String, Long>> roomNumberMap = new HashMap<>();
        for(Object[] object: objects) {
            Map<String, Long> roomsMap = new HashMap<>();
            roomsMap.put("total", (long)object[1]);
            roomsMap.put("available", (long)object[0]);
            roomNumberMap.put((char)object[2], roomsMap);
        }
        return roomNumberMap;
    }

    @RequestMapping(value = "/user/count", method = RequestMethod.GET)
    public @ResponseBody Map<String, Long> getUsers() {
        List<Object[]> objectList = userRepository.findUserNumbers();
        Map<String, Long> userMap = new HashMap<>();
        for(Object[] object: objectList) {
            userMap.put((String)object[1], (long)object[0]);
        }
        return userMap;
    }

    @RequestMapping(value = "/appointment/count", method = RequestMethod.GET)
    public @ResponseBody Map<LocalDate, Long> getDailyCount(@RequestParam(name = "days")Optional<Integer> days) {
        int noDays = 10;
        if(days.isPresent()) {
            noDays = days.get();
        }
        LocalDate date = LocalDate.now().minusDays(noDays);

        List<Object[]> objects = appointmentRepository.findDailyCount(date);
        Map<LocalDate, Long> dailyMap = new HashMap<>();
        for(Object[] object: objects) {
            dailyMap.put((LocalDate) object[1], (long)object[0]);
        }
        return dailyMap;
    }

    /* Private methods */
//    private void generateAnalysisData() {
//        List<Object[]> objectList = appointmentRepository.findAllAppointmentType();
//        Map<Character, Long> appointmentTypeMap = new HashMap<>();
//        for(Object[] object: objectList) {
//            appointmentTypeMap.put((char)object[0], (long)object[1]);
//        }
//    }
}
