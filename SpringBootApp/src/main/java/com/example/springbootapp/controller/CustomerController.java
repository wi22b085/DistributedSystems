package com.example.springbootapp.controller;

import com.example.springbootapp.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
    private int id;
    @Autowired
    public CustomerController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @GetMapping("/details/{id}")
    public void getCustomer(@PathVariable int id) {
            this.id=id;
            rabbit.convertAndSend(RabbitMQConfig.ECHO_IN_QUEUE_ID, id);

    }
    public String  checkFile(int id){
        File f = new File("./../File Storage/");
        File[] files = f.listFiles((dir, name) -> name.startsWith(String.valueOf(id)) && name.endsWith(".pdf")&& name.contains(LocalDate.now().toString()));
        System.out.println(files);
        if (files != null && files.length > 0) {
            return files[0].getAbsolutePath();
        } else {
            return null;
        }
    }


    @PostMapping("/pdfValue")
    public String sendInvoice(Path path){
        String pdfPath=checkFile(id);

        if (pdfPath != null) {
            return pdfPath;
        } else {
            return "PDF not found for customer ID: " + id;
        }
    }

}