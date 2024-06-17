package com.example.datacollectionreciever.services;

import com.example.datacollectionreciever.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SortData {

    private final RabbitTemplate rabbit;

    @Autowired
    public SortData(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = RabbitMQConfig.ECHO_OUT_USER_ID)
    public void receiveMessage(String message) {
        System.out.println("Message sent to Data Collection Receiver, that a new job started for Customer with ID = " + message + ".");
    }

    private final Map<String, String[]> messageStore = new HashMap<>();

    @RabbitListener(queues = RabbitMQConfig.ECHO_OUT_QUEUE_VALUE)
    public void sendDataPdf(String message) {
        String[] parts = message.split(",");
        double sum = 0;
        String customerId = "";
        String chargingStation = "";
        String sumValue = "0";

        for (String part : parts) {
            if (part.startsWith("summe:")) {
                sumValue = part.split(":")[1];
                sum = Double.parseDouble(sumValue);
            } else if (part.startsWith("customerId:")) {
                customerId = part.split(":")[1];
            } else if (part.startsWith("chargingStation:")) {
                chargingStation = part.split(":")[1];
            }
        }

        String key = customerId + "-" + chargingStation;

        messageStore.putIfAbsent(key, new String[3]);

        String[] storedData = messageStore.get(key);


        storedData[0] = sumValue;
        storedData[1] = customerId;
        storedData[2] = chargingStation;

        if (messageStore.containsKey(customerId + "-1") && messageStore.containsKey(customerId + "-2") && messageStore.containsKey(customerId + "-3")) {

            String[] message1 = messageStore.get(customerId + "-1");
            String[] message2 = messageStore.get(customerId + "-2");
            String[] message3 = messageStore.get(customerId + "-3");

            double sum1 = Double.parseDouble(message1[0]);
            double sum2 = Double.parseDouble(message2[0]);
            double sum3 = Double.parseDouble(message3[0]);

            double totalSum = sum1 + sum2 + sum3;

            String cost1 = String.valueOf(sum1 * 2);
            String cost2 = String.valueOf(sum2 * 2);
            String cost3 = String.valueOf(sum3 * 2);
            String totalCost = String.valueOf(totalSum * 2);

            String sumString1 = String.valueOf(sum1);
            String sumString2 = String.valueOf(sum2);
            String sumString3 = String.valueOf(sum3);

            String totalSumString = String.valueOf(totalSum);

            LocalDateTime currentDateTime = LocalDateTime.now();
            String datetime_invoice = currentDateTime.toString();
            datetime_invoice = datetime_invoice.replace(":", "_");
            datetime_invoice = datetime_invoice.split("\\.")[0];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm");
            String datetime = currentDateTime.format(formatter);



            String pdfMessage = "datetime:" + datetime + ",datetime_invoice:" + datetime_invoice + ",sumString1:" + sumString1 + ",sumString2:" + sumString2 + ",sumString3:" + sumString3 + ",totalSumString:" + totalSumString + ",cost1:" + cost1 + ",cost2:" + cost2 + ",cost3:" + cost3 + ",totalCost:" + totalCost + ",customerId:" + customerId;
            rabbit.convertAndSend(RabbitMQConfig.ECHO_OUT_DATA_PDF, pdfMessage);

            messageStore.remove(customerId + "-1");
            messageStore.remove(customerId + "-2");
            messageStore.remove(customerId + "-3");

        }
    }

    public Map<String, String[]> getMessageStore() {
        return messageStore;
    }


}
