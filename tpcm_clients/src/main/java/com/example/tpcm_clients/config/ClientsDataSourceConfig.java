package com.example.tpcm_clients.config;

import com.example.tpcm_clients.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.tpcm_clients.repository.clients",
        entityManagerFactoryRef = "clientsEntityManagerFactory",
        transactionManagerRef = "clientsTransactionManager"
)
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
                "HikariPool-clients",
                null
        );
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean clientsEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(clientsDataSource())
                .packages("com.example.tpcm_clients.models.clients")
                .persistenceUnit("clients")
                .build();
    }

    @Bean
    public PlatformTransactionManager clientsTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(
                Objects.requireNonNull(clientsEntityManagerFactory(builder).getObject())
        );
    }

    @Bean(name = "clientsJdbc")
    public JdbcTemplate clientsJdbcTemplate(@Qualifier("clientsDs") DataSource clientsDs) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(clientsDs);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }
}

