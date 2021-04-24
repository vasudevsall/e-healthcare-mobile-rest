package com.ehealthcaremanagement.models.custom;

import java.time.LocalDate;

public class AppointmentRequestModel {

    private String doctorUsername;
    private LocalDate date;
    private char slot;

    public AppointmentRequestModel(String doctorUsername, LocalDate date, char slot) {
        this.doctorUsername = doctorUsername;
        this.date = date;
        this.slot = slot;
    }

    public AppointmentRequestModel() {}

    public String getDoctorUsername() {
        return doctorUsername;
    }

    public void setDoctorUsername(String doctorUsername) {
        this.doctorUsername = doctorUsername;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public char getSlot() {
        return slot;
    }

    public void setSlot(char slot) {
        this.slot = slot;
    }
}
