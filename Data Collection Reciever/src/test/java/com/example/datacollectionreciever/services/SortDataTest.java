package com.example.datacollectionreciever.services;

import com.example.datacollectionreciever.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SortDataTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private SortData sortData;

    @Test
    public void testSendPdfData() throws SQLException {

        // Arrange
        String message1 = "summe:14.0,customerId:2,chargingStation:1";
        String message2 = "summe:4.18,customerId:2,chargingStation:2";

        String message4 = "summe:3.0,customerId:1,chargingStation:1";

        String message3 = "summe:0.0,customerId:2,chargingStation:3";

        LocalDateTime currentDateTime = LocalDateTime.now();

        String datetime_invoice = currentDateTime.toString();
        datetime_invoice = datetime_invoice.replace(":", "_");
        datetime_invoice = datetime_invoice.split("\\.")[0];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm");
        String datetime = currentDateTime.format(formatter);

        // Act
        sortData.sendDataPdf(message1);
        sortData.sendDataPdf(message2);

        sortData.sendDataPdf(message4);

        sortData.sendDataPdf(message3);

        // Assert
        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate, times(1)).convertAndSend(queueCaptor.capture(), messageCaptor.capture());

        assertThat(queueCaptor.getValue()).isEqualTo(RabbitMQConfig.ECHO_OUT_DATA_PDF);
        assertThat(messageCaptor.getValue()).isEqualTo("datetime:" + datetime + ",datetime_invoice:" + datetime_invoice + ",sumString1:14.0,sumString2:4.18,sumString3:0.0,totalSumString:18.18,cost1:28.0,cost2:8.36,cost3:0.0,totalCost:36.36,customerId:2");

        assertThat(sortData.getMessageStore().containsKey("2-1")).isFalse();
        assertThat(sortData.getMessageStore().containsKey("2-2")).isFalse();
        assertThat(sortData.getMessageStore().containsKey("2-3")).isFalse();

        assertThat(sortData.getMessageStore().containsKey("1-1")).isTrue();

    }

}
