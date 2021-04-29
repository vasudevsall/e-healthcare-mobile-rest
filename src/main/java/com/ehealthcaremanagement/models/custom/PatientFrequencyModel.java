package com.ehealthcaremanagement.models.custom;

import com.ehealthcaremanagement.models.repository.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

public class PatientFrequencyModel {

    private long count;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private UserModel user;

    public PatientFrequencyModel(long count, UserModel user) {
        this.count = count;
        this.user = user;
    }

    public PatientFrequencyModel(){}

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
