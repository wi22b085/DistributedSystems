package com.example.datacollectionreciever.config;

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


    public static final String ECHO_OUT_QUEUE_VALUE = "sendValue";

    public static final String ECHO_OUT_USER_ID = "forwardUserDetails";

    public static final String ECHO_OUT_DATA_PDF = "sendPdfData";

    @Bean
    public Queue echoOutId() {
        return new Queue(ECHO_OUT_USER_ID, false);
    }
    @Bean
    public Queue echoOutValue() {
        return new Queue(ECHO_OUT_QUEUE_VALUE, false);
    }
    @Bean
    public Queue echoOutData_pdf() {
        return new Queue(ECHO_OUT_DATA_PDF, false);
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
        //rabbitTemplate.setDefaultReceiveQueue(ECHO_IN_URL_ID);
        return rabbitTemplate;
    }


}
