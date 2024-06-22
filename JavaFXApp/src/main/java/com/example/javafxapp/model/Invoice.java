package com.example.javafxapp.model;

import javafx.scene.control.Button;

public class Invoice {
    private String customerId;
    private Button viewButton;

    public Invoice(String customerId, Button viewButton) {
        this.customerId = customerId;
        this.viewButton = viewButton;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Button getViewButton() {
        return viewButton;
    }

    public void setViewButton(Button viewButton) {
        this.viewButton = viewButton;
    }
}
