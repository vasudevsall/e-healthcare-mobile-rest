package com.ehealthcaremanagement.models.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class ProductModel {

    @Id
    private long id;
    private String name;
    private String composition;
    @Column(name = "`use`")
    private long use; // Number of average units used per week
    private long weeks; // Number of weeks for above average

    public ProductModel(String name, String composition, long use, long weeks) {
        this.name = name;
        this.composition = composition;
        this.use = use;
        this.weeks = weeks;
    }

    public ProductModel(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public long getUse() {
        return use;
    }

    public void setUse(long use) {
        this.use = use;
    }

    public long getWeeks() {
        return weeks;
    }

    public void setWeeks(long weeks) {
        this.weeks = weeks;
    }
}
