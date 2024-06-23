package com.example.stationdatacollector.services;

import com.example.stationdatacollector.config.RabbitMQConfig;
import com.example.stationdatacollector.entity.ChargeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecieveCustomerDataTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    @Qualifier("Db1")
    private JdbcTemplate jdbcTemplate1;

    @Mock
    @Qualifier("Db2")
    private JdbcTemplate jdbcTemplate2;

    @Mock
    @Qualifier("Db3")
    private JdbcTemplate jdbcTemplate3;

    @InjectMocks
    private RecieveCustomerData receiveCustomerData;

    @BeforeEach
    void setUp() {
        receiveCustomerData = new RecieveCustomerData(rabbitTemplate, jdbcTemplate1, jdbcTemplate2, jdbcTemplate3);
    }

    @Test
    public void testGetCustomerData() throws SQLException {
        // Arrange
        String message = "customerId:1,url:30011";
        ChargeEntity chargeEntity1 = new ChargeEntity(1, 100.0, 1);
        ChargeEntity chargeEntity2 = new ChargeEntity(2, 20.0, 1);
        ChargeEntity chargeEntity3 = new ChargeEntity(3, 31.1, 1);

        List<ChargeEntity> mockCharges = List.of(chargeEntity1, chargeEntity2, chargeEntity3);
        when(jdbcTemplate1.query(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(mockCharges);

        // Act
        receiveCustomerData.getCustomerData(message);

        // Assert

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.ECHO_OUT_QUEUE_VALUE),
                eq((Object) "summe:151.1,customerId:1,chargingStation:1")
        );
    }
    @Test
    public void testGetCustomerData2() throws SQLException {
        // Arrange
        String message = "customerId:1,url:30012";
        ChargeEntity chargeEntity1 = new ChargeEntity(1, 100.1, 1);
        ChargeEntity chargeEntity2 = new ChargeEntity(2, 20.0, 1);
        ChargeEntity chargeEntity3 = new ChargeEntity(3, 31.1, 1);

        List<ChargeEntity> mockCharges = List.of(chargeEntity1, chargeEntity2, chargeEntity3);
        when(jdbcTemplate2.query(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(mockCharges);



        // Act
        receiveCustomerData.getCustomerData(message);

        // Assert
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.ECHO_OUT_QUEUE_VALUE),
                eq((Object) "summe:151.2,customerId:1,chargingStation:2")
        );
    }
    @Test
    public void testGetCustomerData3() throws SQLException {
        // Arrange
        String message = "customerId:1,url:30013";
        ChargeEntity chargeEntity1 = new ChargeEntity(1, 100.2, 1);
        ChargeEntity chargeEntity2 = new ChargeEntity(2, 20.0, 1);
        ChargeEntity chargeEntity3 = new ChargeEntity(3, 31.1, 1);

        List<ChargeEntity> mockCharges = List.of(chargeEntity1, chargeEntity2, chargeEntity3);
        when(jdbcTemplate3.query(anyString(), any(RowMapper.class), eq(1)))
                .thenReturn(mockCharges);

        // Act
        receiveCustomerData.getCustomerData(message);

        // Assert

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.ECHO_OUT_QUEUE_VALUE),
                eq((Object) "summe:151.3,customerId:1,chargingStation:3")
        );

    }
}
