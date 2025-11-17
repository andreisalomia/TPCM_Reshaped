package com.example.tpcmscheduler.config;

import com.example.tpcmscheduler.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClientsDataSourceConfig {

    @Bean
    @ConfigurationProperties("clients.datasource")
    public DataSourceProperties clientsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "clientsDs")
    public DataSource clientsDataSource() {
        return DataSourceUtil.createHikariDataSource(
                clientsDataSourceProperties(),
                "HikariPool-scheduler-clients",
                null
        );
    }

    @Bean(name = "clientsJdbc")
    public JdbcTemplate clientsJdbcTemplate(@Qualifier("clientsDs") DataSource clientsDs) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(clientsDs);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }
}