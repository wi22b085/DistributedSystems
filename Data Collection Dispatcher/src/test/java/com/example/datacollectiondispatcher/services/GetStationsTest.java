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
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate, times(3)).convertAndSend(routingKeyCaptor.capture(), messageCaptor.capture());

        List<String> capturedMessages = messageCaptor.getAllValues();
        assertThat(capturedMessages).containsExactly(
                "customer:1,url:db_url_1",
                "customer:1,url:db_url_2",
                message
        );
    }

}