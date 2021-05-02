package com.ehealthcaremanagement.models.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "admission")
public class AdmissionModel {

    @Id
    private long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private RoomModel room;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private UserModel user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private DoctorModel doctor;

    private String diagnosis;
    private String description;
    private LocalDate admit;
    private LocalDate discharge;
    private double cost;

    public AdmissionModel(RoomModel room, UserModel user, DoctorModel doctor, String diagnosis, String description, LocalDate admit, LocalDate discharge, double cost) {
        this.room = room;
        this.user = user;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.description = description;
        this.admit = admit;
        this.discharge = discharge;
        this.cost = cost;
    }

    public AdmissionModel() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public DoctorModel getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorModel doctor) {
        this.doctor = doctor;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAdmit() {
        return admit;
    }

    public void setAdmit(LocalDate admit) {
        this.admit = admit;
    }

    public LocalDate getDischarge() {
        return discharge;
    }

    public void setDischarge(LocalDate discharge) {
        this.discharge = discharge;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
