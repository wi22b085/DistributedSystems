package com.example.springbootapp.controller;

import com.example.springbootapp.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

@RestController
public class CustomerController {
    private final RabbitTemplate rabbit;
    @Autowired
    public CustomerController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @GetMapping("/invoices/{id}")
    public void getCustomer(@PathVariable int id) {
            rabbit.convertAndSend(RabbitMQConfig.ECHO_IN_QUEUE_ID, id);

    }
    public String  checkFile(int id){
        File f = new File("./../File Storage/");
        File[] files = f.listFiles((dir, name) -> name.startsWith(String.valueOf(id)) && name.endsWith(".pdf")&& name.contains(LocalDate.now().toString()));
        System.out.println(files);
        if (files != null && files.length > 0) {
            return files[0].getPath();
        } else {
            return null;
        }
    }

    @PostMapping("/invoices/{id}")
    public ResponseEntity<String>  sendInvoice(@PathVariable int id){
        String pdfPath=checkFile(id);

        if (pdfPath != null) {
            return ResponseEntity.ok(pdfPath);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}