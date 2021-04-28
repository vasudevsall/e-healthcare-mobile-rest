package com.ehealthcaremanagement.models.custom;

public class AppointmentDetailsUpdateModel {

    private String username;
    private long appointment;
    private String diagnosis;
    private String prescription;

    public AppointmentDetailsUpdateModel(String username, long appointment, String diagnosis, String prescription) {
        this.username = username;
        this.appointment = appointment;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
    }

    public AppointmentDetailsUpdateModel() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAppointment() {
        return appointment;
    }

    public void setAppointment(long appointment) {
        this.appointment = appointment;
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
