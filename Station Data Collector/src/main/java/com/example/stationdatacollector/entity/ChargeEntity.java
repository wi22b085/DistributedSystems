package com.example.stationdatacollector.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "customerDetails")
public class ChargeEntity {
    public ChargeEntity(int id, double kwh, int customerId) {
        this.id = id;
        this.customerId = customerId;
        this.kwh = kwh;
    }

    @Id
    private int id;
    private int customerId;
    private double kwh;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }




}
