package com.example.tpcmscheduler.config;

import com.example.tpcmscheduler.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class AppDataSourceConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    @Primary
    @Bean
    @ConfigurationProperties("app.datasource")
    public DataSourceProperties appDataSourceProperties() {
        return new DataSourceProperties();
    }

    private String buildAppIdentifier() {
        return applicationName + " " + applicationVersion;
    }

    @Primary
    @Bean(name = "appDs")
    public DataSource appDataSource() {
        Properties sessionProps = new Properties();
        sessionProps.put("v$session.program", buildAppIdentifier());

        return DataSourceUtil.createHikariDataSource(
                appDataSourceProperties(),
                "HikariPool-scheduler-app",
                sessionProps
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