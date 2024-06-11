package com.example.datacollectiondispatcher.services;

import com.example.datacollectiondispatcher.config.RabbitMQConfig;
import com.example.datacollectiondispatcher.entity.ServerEntities;
import com.example.datacollectiondispatcher.repository.StationsRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetStations {


    private final RabbitTemplate rabbit;
    private final StationsRepository stationsRepository;

    @Autowired
    public GetStations(RabbitTemplate rabbit, StationsRepository stationsRepository) {
        this.rabbit = rabbit;
        this.stationsRepository = stationsRepository;
    }
    @RabbitListener(queues = RabbitMQConfig.ECHO_IN_QUEUE_ID)
    public void sendStations(String message) {
        List<ServerEntities> servers = stationsRepository.findAll().stream().map(
                serverEntities -> new ServerEntities(
                        serverEntities.getDb_url()
                )
        ).toList();
        // Delay
        try {
            Thread.sleep(message.length() * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Delete2ndChar service interrupted");
        }
        String temp;
        for (ServerEntities server : servers) {
            temp="customer:"+message+",url:"+server.getDb_url();
            rabbit.convertAndSend(RabbitMQConfig.ECHO_IN_URL_ID, temp.toString());
        }
        rabbit.convertAndSend(RabbitMQConfig.ECHO_OUT_USER_ID, message.toString());
    }

}
