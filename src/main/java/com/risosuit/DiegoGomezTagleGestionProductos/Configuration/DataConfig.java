package com.risosuit.DiegoGomezTagleGestionProductos.Configuration;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("oracle.jdbc.OracleDriver")
                .url("jdbc:oracle:thin:@localhost:1521/orcl")
                .username("DiegoGomezTGestionProductos")
                .password("password1")
                .build();
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSourse){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourse);
        return jdbcTemplate;
    }

}
