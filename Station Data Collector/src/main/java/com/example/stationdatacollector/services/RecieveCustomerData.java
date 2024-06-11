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
    @RabbitListener(queues = RabbitMQConfig.ECHO_IN_URL_ID)
    public void getCustomerData(String message) {
        System.out.println(message);
        var sql= """
                    SELECT id, kwh,customer_id
                    FROM Charge 
                    Where customer_id=?
                    """;


        String[] parts = message.split(",");
        int customerId = Integer.parseInt(parts[0].split(":")[1].trim());
        List<ChargeEntity> output=new ArrayList<>();
        if (message.contains("30011")){
             output = jdbcTemplate1.query(sql, (ResultSet rs, int rownumb)-> new ChargeEntity(
                    rs.getInt("id"),
                    rs.getDouble("kwh"),
                     rs.getInt("customer_id")
                     ),customerId);
        } else if (message.contains("30012")) {
           output = jdbcTemplate2.query(sql, (ResultSet rs, int rownumb)-> new ChargeEntity(
                   rs.getInt("id"),
                   rs.getDouble("kwh"),
                   rs.getInt("customer_id")
            ),customerId);

        } else if (message.contains("30013")) {
            output = jdbcTemplate3.query(sql, (ResultSet rs, int rownumb)-> new ChargeEntity(
                    rs.getInt("id"),
                    rs.getDouble("kwh"),
                    rs.getInt("customer_id")
            ),customerId);
        }
        double sum=0;
        for (ChargeEntity out : output) {
            sum+=out.getKwh();
        }

        String msg="summe:"+sum+",customerId:"+customerId;
        System.out.println(msg);

        rabbit.convertAndSend(RabbitMQConfig.ECHO_OUT_QUEUE_VALUE, msg );

    }
}
