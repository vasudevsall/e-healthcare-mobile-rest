package com.ehealthcaremanagement.models.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointment")
public class AppointmentModel {

    @Id
    private long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private UserModel userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private DoctorModel doctorId;

    private LocalDate date;
    private char slot;
    private char type;
    private double blocks;
    private int token;

    public AppointmentModel(UserModel userId, DoctorModel doctorId, LocalDate date, char slot, char type, double blocks) {
        this.userId = userId;
        this.doctorId = doctorId;
        this.date = date;
        this.type = type;
        this.slot = slot;
        this.blocks = blocks;
    }

    public AppointmentModel() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserModel getUserId() {
        return userId;
    }

    public void setUserId(UserModel userId) {
        this.userId = userId;
    }

    public DoctorModel getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(DoctorModel doctorId) {
        this.doctorId = doctorId;
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

    public double getBlocks() {
        return blocks;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public void setBlocks(double blocks) {
        this.blocks = blocks;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
