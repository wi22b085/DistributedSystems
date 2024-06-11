package com.example.springbootapp.controller;

import com.example.springbootapp.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final RabbitTemplate rabbit;
    @Autowired
    public CustomerController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @GetMapping("/details/{id}")
    public void getCustomer(@PathVariable int id) {

        rabbit.convertAndSend(RabbitMQConfig.ECHO_IN_QUEUE_ID, id );
    }

}