package com.ehealthcaremanagement.models.repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "room")
public class RoomModel {

    @Id
    private long id;

    private int floor;
    private int beds;
    private int max;
    private char type;
    private double price;

    public RoomModel(long id, int floor, int beds, int max, char type, double price) {
        this.id = id;
        this.floor = floor;
        this.beds = beds;
        this.max = max;
        this.type = type;
        this.price = price;
    }
    public RoomModel() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
