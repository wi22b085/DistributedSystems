package com.example.stationdatacollector.services;
import com.example.stationdatacollector.config.DataSourceConfig;
import com.example.stationdatacollector.config.RabbitMQConfig;
import com.example.stationdatacollector.entity.ChargeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RecieveCustomerData {

    private final RabbitTemplate rabbit;
    @Autowired
    DataSourceConfig dataSourceConfig;
    private final JdbcTemplate jdbcTemplate1;
    private final JdbcTemplate jdbcTemplate2;
    private final JdbcTemplate jdbcTemplate3;

    @Autowired
    public RecieveCustomerData(RabbitTemplate rabbit, @Qualifier("Db1")JdbcTemplate jdbcTemplate1, @Qualifier("Db2")JdbcTemplate jdbcTemplate2, @Qualifier("Db3")JdbcTemplate jdbcTemplate3) {
        this.rabbit = rabbit;
        this.jdbcTemplate1=jdbcTemplate1;
        this.jdbcTemplate2=jdbcTemplate2;
        this.jdbcTemplate3=jdbcTemplate3;

    }
    @RabbitListener(queues = {RabbitMQConfig.ECHO_IN_QUEUE_CUSTOMER_DETALS,RabbitMQConfig.ECHO_OUT_DB_DATA})
    public void getCustomerData(String message) {
        var sql= """
                    SELECT id, customerid,kwh
                    fROM Charge 
                    Where customerid=?
                    """;

        String[] parts = message.split(",");
        int customerId = Integer.parseInt(parts[0].split(":")[1].trim());
        List<ChargeEntity> output=new ArrayList<>();
        if (message.contains("30011")){
             output = jdbcTemplate1.query(sql, (ResultSet rs, int rownumb)-> new ChargeEntity(
                    rs.getInt("id"),
                    rs.getDouble("kwh"),
                    rs.getInt("customerid")
            ),customerId);
        } else if (message.contains("30012")) {
           output = jdbcTemplate2.query(sql, (ResultSet rs, int rownumb)-> new ChargeEntity(
                    rs.getInt("id"),
                    rs.getDouble("kwh"),
                    rs.getInt("customerid")
            ),customerId);

        } else if (message.contains("30013")) {
            output = jdbcTemplate3.query(sql, (ResultSet rs, int rownumb)-> new ChargeEntity(
                    rs.getInt("id"),
                    rs.getDouble("kwh"),
                    rs.getInt("customerid")
            ),customerId);
        }
        double sum = output.stream()
                .mapToDouble(ChargeEntity::getKwh)
                .sum();
        // Delay
        try {
            Thread.sleep(message.length() * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Delete2ndChar service interrupted");
        }
        String msg="summe:"+sum+",customerId:"+customerId;

        rabbit.convertAndSend(RabbitMQConfig.ECHO_OUT_QUEUE_NAME, msg );

    }
}
