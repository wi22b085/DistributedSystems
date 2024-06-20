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

@RestController
public class CustomerController {
    private final RabbitTemplate rabbit;
    @Autowired
    public CustomerController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @GetMapping("/details/{id}")
    public void getCustomer(@PathVariable int id) {
        if (checkFile(id)){

        }else {
            rabbit.convertAndSend(RabbitMQConfig.ECHO_IN_QUEUE_ID, id);
        }
    }
    public boolean checkFile(int id){
        boolean val=true;
        File f = new File("../../../../../../FileStorage/"+id+".pdf");
        if(f.exists() && !f.isDirectory()) {
            Path path=f.toPath();
            sendInvoice(path);
            return true;
        }


        return val;
    }


    @PostMapping("/pdfValue")
    public File sendInvoice(Path path){


        return ;
    }


}