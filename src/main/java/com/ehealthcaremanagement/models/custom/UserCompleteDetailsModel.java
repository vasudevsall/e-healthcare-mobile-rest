package com.ehealthcaremanagement.models.custom;

import com.ehealthcaremanagement.models.repository.AdmissionModel;
import com.ehealthcaremanagement.models.repository.AppointmentDetailsModel;
import com.ehealthcaremanagement.models.repository.UserModel;

import java.util.List;

public class UserCompleteDetailsModel {

    private UserModel userModel;
    List<AppointmentDetailsModel> appointmentDetailsModelList;
    List<AdmissionModel> admissionModels;

    //TODO: Complete User Details after Accommodations
}
