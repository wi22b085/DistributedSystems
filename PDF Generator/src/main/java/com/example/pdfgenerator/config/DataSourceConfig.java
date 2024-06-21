package com.example.pdfgenerator.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean(name="Db")
    public JdbcTemplate jdbcTemplate(){
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:30001/customerdb");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");
        DataSource dataSource=new HikariDataSource(hikariConfig);
        return new JdbcTemplate(dataSource);
    }
}
