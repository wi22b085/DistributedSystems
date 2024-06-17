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

import static org.assertj.core.api.Assertions.assertThat;
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
    private RecieveCustomerData recieveCustomerData;

    @BeforeEach
    void setUp() {
        // Initialize the RecieveCustomerData with mocks
        recieveCustomerData = new RecieveCustomerData(rabbitTemplate, jdbcTemplate1, jdbcTemplate2, jdbcTemplate3);
    }

    @Test
    public void testGetCustomerData_Db1() throws SQLException {
        // Arrange
        String message = "customerId:1,url:30011";
        String sql = "SELECT id, kwh,customer_id FROM Charge Where customer_id=?";
        List<ChargeEntity> mockCharges = List.of(new ChargeEntity(1, 100.0, 1));
        when(jdbcTemplate1.query(eq(sql), any(Object[].class), any(RowMapper.class))).thenReturn(mockCharges);

        // Act
        recieveCustomerData.getCustomerData(message);

        // Assert
        verify(jdbcTemplate1, times(1)).query(eq(sql), any(Object[].class), any(RowMapper.class));

        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate, times(1)).convertAndSend(queueCaptor.capture(), messageCaptor.capture());

        assertThat(queueCaptor.getValue()).isEqualTo(RabbitMQConfig.ECHO_OUT_QUEUE_VALUE);
        assertThat(messageCaptor.getValue()).isEqualTo("summe:100.0,customerId:1,chargingStation:1");
    }
}
