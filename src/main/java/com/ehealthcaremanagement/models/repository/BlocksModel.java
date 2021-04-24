package com.ehealthcaremanagement.models.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "blocks")
/*
* This table stores the total number of time-blocks (each block = 5min) booked
* for a particular doctor, date and timeslot
* */
public class BlocksModel {

    @Id
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private DoctorModel doctorModel;

    private LocalDate date;
    private char slot;
    private double blocks;
    private int patients;

    public BlocksModel(DoctorModel doctorModel, LocalDate date, char slot, double blocks, int patients) {
        this.doctorModel = doctorModel;
        this.date = date;
        this.slot = slot;
        this.blocks = blocks;
        this.patients = patients;
    }

    public BlocksModel() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DoctorModel getDoctorModel() {
        return doctorModel;
    }

    public void setDoctorModel(DoctorModel doctorModel) {
        this.doctorModel = doctorModel;
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

    public void setBlocks(double blocks) {
        this.blocks = blocks;
    }

    public int getPatients() {
        return patients;
    }

    public void setPatients(int patients) {
        this.patients = patients;
    }

}
