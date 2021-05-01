package com.ehealthcaremanagement.services;

import com.ehealthcaremanagement.models.custom.DoctorAnalysisModel;
import com.ehealthcaremanagement.models.custom.PatientFrequencyModel;
import com.ehealthcaremanagement.models.repository.BlocksModel;
import com.ehealthcaremanagement.models.repository.DoctorModel;
import com.ehealthcaremanagement.repositories.AppointmentRepository;
import com.ehealthcaremanagement.repositories.BlocksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DoctorAnalysisService {

    @Autowired
    private FindModel findModel;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private BlocksRepository blocksRepository;

    private DoctorModel doctorModel;
    private LocalDate date;

    public DoctorAnalysisModel createDoctorAnalysis(Principal principal, int days) {
        this.doctorModel = findModel.findDoctorModel(principal.getName());
        findDate(days);
        List<BlocksModel> blocksModels = blocksRepository.findAllByDoctorModelAndDateAfter(doctorModel, date);

        int totalAppointments = 0;
        int cancelledAppointments = 0;
        int operations = 0;
        int patientsAdmitted = 0;
        int videoConsultations = 0;

        for(BlocksModel blocksModel: blocksModels) {
            totalAppointments += blocksModel.getPatients();
            cancelledAppointments += blocksModel.getCancelled();
        }
        //TODO operations, patients Admitted and videoConsultations

        List<PatientFrequencyModel> regular = regularPatients();


        return new DoctorAnalysisModel(
                doctorModel, totalAppointments, cancelledAppointments,
                operations, patientsAdmitted, videoConsultations,
                regular
        );
    }

    private List<PatientFrequencyModel> regularPatients() {
        return appointmentRepository.findRegularPatients(doctorModel, date);
    }

    private void findDate(int days) {
        this.date = LocalDate.now().minusDays(days);
    }

}
