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
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:30011/springrest-stationdb");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");
        DataSource dataSource=new HikariDataSource(hikariConfig);
        return new JdbcTemplate(dataSource);
    }
    @Bean(name="Db2")
    public JdbcTemplate jdbcTemplate2(){
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:30012/springrest-stationdb");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");
        DataSource dataSource=new HikariDataSource(hikariConfig);
        return new JdbcTemplate(dataSource);
    }
    @Bean(name="Db3")
    public JdbcTemplate jdbcTemplate3(){
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:30013/springrest-stationdb");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");
        DataSource dataSource=new HikariDataSource(hikariConfig);
        return new JdbcTemplate(dataSource);
    }

}
