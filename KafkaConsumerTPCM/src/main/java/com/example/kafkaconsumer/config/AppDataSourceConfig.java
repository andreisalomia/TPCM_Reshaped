package com.example.kafkaconsumer.config;

import com.example.kafkaconsumer.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.kafkaconsumer.repository.app",
        entityManagerFactoryRef = "appEntityManagerFactory",
        transactionManagerRef = "appTransactionManager"
)
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
                "HikariPool-app",
                sessionProps
        );
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(appDataSource())
                .packages("com.example.kafkaconsumer.model.app")
                .persistenceUnit("app")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager appTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(
                Objects.requireNonNull(appEntityManagerFactory(builder).getObject())
        );
    }

    @Bean(name = "appJdbc")
    public JdbcTemplate appJdbcTemplate(@Qualifier("appDs") DataSource appDs) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(appDs);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }
}