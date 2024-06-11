package com.example.datacollectiondispatcher.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;


    public static final String ECHO_IN_QUEUE_ID = "sendCustomerDetails";
    public static final String ECHO_IN_URL_ID = "sendUrlDetails";
    public static final String ECHO_OUT_USER_ID = "forwardUserDetails";

    @Bean
    public Queue echoInQueue() {
        return new Queue(ECHO_IN_QUEUE_ID, false);
    }
    @Bean
    public Queue echoInUrl() {
        return new Queue(ECHO_IN_URL_ID, false);
    }
    @Bean
    public Queue echoOutId() {
        return new Queue(ECHO_OUT_USER_ID, false);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setDefaultReceiveQueue(ECHO_IN_QUEUE_ID);
        return rabbitTemplate;
    }
}

