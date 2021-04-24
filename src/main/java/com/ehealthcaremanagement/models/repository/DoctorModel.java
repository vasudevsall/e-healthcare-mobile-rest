package com.ehealthcaremanagement.models.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "doctor")
public class DoctorModel {

    @Id
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private UserModel userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speciality", referencedColumnName = "speciality")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private SpecialitiesModel speciality;

    @Column(name = "morning")
    private double morningBlocks;

    @Column(name = "afternoon")
    private double afternoonBlocks;

    private String qualification;
    private int experience;

    public DoctorModel(UserModel id, SpecialitiesModel speciality, double morningBlocks, double afternoonBlocks, String qualification, int experience) {
        this.speciality = speciality;
        this.morningBlocks = morningBlocks;
        this.afternoonBlocks = afternoonBlocks;
        this.qualification = qualification;
        this.experience = experience;
    }

    public DoctorModel() {}

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

    public SpecialitiesModel getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SpecialitiesModel speciality) {
        this.speciality = speciality;
    }

    public double getMorningBlocks() {
        return morningBlocks;
    }

    public void setMorningBlocks(double morningBlocks) {
        this.morningBlocks = morningBlocks;
    }

    public double getAfternoonBlocks() {
        return afternoonBlocks;
    }

    public void setAfternoonBlocks(double afternoonBlocks) {
        this.afternoonBlocks = afternoonBlocks;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
