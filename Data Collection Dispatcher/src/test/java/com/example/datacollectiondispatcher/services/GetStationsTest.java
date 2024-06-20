package com.example.datacollectiondispatcher.services;

import com.example.datacollectiondispatcher.config.RabbitMQConfig;
import com.example.datacollectiondispatcher.entity.ServerEntities;
import com.example.datacollectiondispatcher.repository.StationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class GetStationsTest {
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private StationsRepository stationsRepository;

    @InjectMocks
    private GetStations getStations;

    @BeforeEach
    void setUp() {
        // Initialize the GetStations with mocks
        getStations = new GetStations(rabbitTemplate, stationsRepository);
    }

    @Test
    public void testSendStations() {
        // Arrange
        String message = "1";
        ServerEntities serverEntity1 = new ServerEntities("db_url_1");
        ServerEntities serverEntity2 = new ServerEntities("db_url_2");
        when(stationsRepository.findAll()).thenReturn(List.of(serverEntity1, serverEntity2));

        // Act
        getStations.sendStations(message);

        // Assert

        verify(stationsRepository).findAll();
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.ECHO_IN_URL_ID, "customer:1,url:db_url_1");
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.ECHO_IN_URL_ID, "customer:1,url:db_url_2");
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.ECHO_OUT_USER_ID, message);

    }

}