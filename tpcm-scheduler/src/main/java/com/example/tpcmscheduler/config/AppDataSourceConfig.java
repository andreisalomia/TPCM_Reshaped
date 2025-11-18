package com.example.tpcmscheduler.config;

import com.example.tpcmscheduler.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class AppDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("app.datasource")
    public DataSourceProperties appDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "appDs")
    public DataSource appDataSource() {

        return DataSourceUtil.createHikariDataSource(
                appDataSourceProperties(),
                "HikariPool-scheduler-app",
                null
        );
    }

    @Primary
    @Bean(name = "appJdbc")
    public JdbcTemplate appJdbcTemplate(@Qualifier("appDs") DataSource appDs) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(appDs);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }
}