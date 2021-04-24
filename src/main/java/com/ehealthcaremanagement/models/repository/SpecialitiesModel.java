package com.ehealthcaremanagement.models.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "specialities")
public class SpecialitiesModel {

    @Id
    private String speciality;

    private String skill;
    private double time;

    @Column(name = "doctors")
    private int doctorNumber;

    public SpecialitiesModel(String speciality, String skill, double time, int doctorNumber) {
        this.speciality = speciality;
        this.skill = skill;
        this.time = time;
        this.doctorNumber = doctorNumber;
    }

    public SpecialitiesModel() {}

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getDoctorNumber() {
        return doctorNumber;
    }

    public void setDoctorNumber(int doctorNumber) {
        this.doctorNumber = doctorNumber;
    }
}
