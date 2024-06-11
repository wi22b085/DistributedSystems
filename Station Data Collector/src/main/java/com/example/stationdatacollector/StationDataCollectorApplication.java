package com.example.stationdatacollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class StationDataCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(StationDataCollectorApplication.class, args);
	}

}
