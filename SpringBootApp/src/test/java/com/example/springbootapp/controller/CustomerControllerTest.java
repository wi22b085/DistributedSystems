package com.example.springbootapp.controller;

import com.example.springbootapp.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        // Initialize the CustomerController with mocks
        customerController = new CustomerController(rabbitTemplate);
    }

    @Test
    public void testGetCustomer() {
        // Arrange
        int id = 1;

        // Act
        customerController.getCustomer(id);

        // Assert
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.ECHO_IN_QUEUE_ID, id);
    }

    @Test
    public void testSendInvoice_Found() {
        // Arrange
        CustomerController spyController = spy(customerController);
        when(spyController.checkFile(anyInt())).thenReturn("./../File Storage/123.pdf");

        // Act
        ResponseEntity<String> result = spyController.sendInvoice(123);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("./../File Storage/123.pdf", result.getBody());
    }

    @Test
    public void testSendInvoice_NotFound() {
        // Arrange
        CustomerController spyController = spy(customerController);
        when(spyController.checkFile(anyInt())).thenReturn(null);

        // Act
        ResponseEntity<String> result = spyController.sendInvoice(123);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

}
