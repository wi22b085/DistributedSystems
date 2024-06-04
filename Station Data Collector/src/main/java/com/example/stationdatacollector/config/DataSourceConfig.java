package com.example.stationdatacollector.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean(name="Db1")
    public JdbcTemplate jdbcTemplate1(){
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/springrest-station1-db");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");
        DataSource dataSource=new HikariDataSource(hikariConfig);
        return new JdbcTemplate(dataSource);
    }
    @Bean(name="Db2")
    public JdbcTemplate jdbcTemplate2(){
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5433/springrest-station2-db");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");
        DataSource dataSource=new HikariDataSource(hikariConfig);
        return new JdbcTemplate(dataSource);
    }
    @Bean(name="Db3")
    public JdbcTemplate jdbcTemplate3(){
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5434/springrest-station3-db");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");
        DataSource dataSource=new HikariDataSource(hikariConfig);
        return new JdbcTemplate(dataSource);
    }

}
