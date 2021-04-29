package com.ehealthcaremanagement.models.custom;

import com.ehealthcaremanagement.models.repository.DoctorModel;

import java.util.List;

public class DoctorAnalysisModel {

    private DoctorModel doctor;
    private int patients;
    private int cancelled;
    private int operations;
    private int admitted;
    private int video;
    private List<PatientFrequencyModel> regular;

    public DoctorAnalysisModel(DoctorModel doctor, int patients, int cancelled, int operations, int admitted, int video, List<PatientFrequencyModel> regular) {
        this.doctor = doctor;
        this.patients = patients;
        this.cancelled = cancelled;
        this.operations = operations;
        this.admitted = admitted;
        this.video = video;
        this.regular = regular;
    }

    public DoctorAnalysisModel() {}

    public DoctorModel getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorModel doctor) {
        this.doctor = doctor;
    }

    public int getPatients() {
        return patients;
    }

    public void setPatients(int patients) {
        this.patients = patients;
    }

    public int getCancelled() {
        return cancelled;
    }

    public void setCancelled(int cancelled) {
        this.cancelled = cancelled;
    }

    public int getOperations() {
        return operations;
    }

    public void setOperations(int operations) {
        this.operations = operations;
    }

    public int getAdmitted() {
        return admitted;
    }

    public void setAdmitted(int admitted) {
        this.admitted = admitted;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }

    public List<PatientFrequencyModel> getRegular() {
        return regular;
    }

    public void setRegular(List<PatientFrequencyModel> regular) {
        this.regular = regular;
    }
}
