package com.ehealthcaremanagement.models.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "appointment_details")
public class AppointmentDetailsModel {

    @Id
    private long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private UserModel userModel;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private AppointmentModel appointmentModel;

    private String diagnosis;
    private String prescription;

    public AppointmentDetailsModel(UserModel userModel, AppointmentModel appointmentModel, String diagnosis, String prescription) {
        this.userModel = userModel;
        this.appointmentModel = appointmentModel;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
    }

    public AppointmentDetailsModel() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AppointmentModel getAppointmentModel() {
        return appointmentModel;
    }

    public void setAppointmentModel(AppointmentModel appointmentModel) {
        this.appointmentModel = appointmentModel;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
}
