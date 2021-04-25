package com.ehealthcaremanagement.models.custom;

import com.ehealthcaremanagement.models.repository.AppointmentModel;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;

public class AppointmentComparator implements Comparator<AppointmentModel> {

    @Override
    public int compare(AppointmentModel appointmentModel1, AppointmentModel appointmentModel2) {

        double b1 = appointmentModel1.getBlocks();
        double b2 = appointmentModel2.getBlocks();
        if(b1 > b2) {
            return 1;
        } else if(b1 < b2) {
            return -1;
        } else {
            int age1 = getAge(appointmentModel1.getUserId().getBirthDate());
            int age2 = getAge(appointmentModel2.getUserId().getBirthDate());
            return Integer.compare(age1, age2);
        }
    }

    public int getAge(LocalDate date) {
        Period period = Period.between(LocalDate.now(), date);
        return period.getYears();
    }
}
