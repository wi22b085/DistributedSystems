package com.example.stationdatacollector.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class RabbitMQConfig {
    public static final String ECHO_IN_QUEUE_CUSTOMER_DETALS = "recieveCustomerRequest";
    public static final String ECHO_OUT_QUEUE_NAME = "Reverse";
    public static final String ECHO_OUT_DB_DATA = "MessageCount";

    @Bean
    public Queue echoInQueue() {
        return new Queue(ECHO_IN_QUEUE_CUSTOMER_DETALS, false);
    }
    @Bean
    public Queue echoOutQueue() { return new Queue(ECHO_OUT_QUEUE_NAME, false); }
    @Bean
    public Queue removeSpacesQueue() { return new Queue(ECHO_OUT_DB_DATA, false); }

}
