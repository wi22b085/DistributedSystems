package com.example.javafxapp;

import com.example.javafxapp.model.Invoice;
import javafx.scene.control.Button;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    Invoice invoice = new Invoice("1", null);

    @Test
    void getCustomerId() {
        Assertions.assertEquals("1", invoice.getCustomerId());
    }

    @Test
    void getViewButton() {
        Assertions.assertNull(invoice.getViewButton());
    }
}
