package com.example.tpcm_clients.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.util.Properties;

public class DataSourceUtil {

    public static DataSource createHikariDataSource(DataSourceProperties props, String poolName, Properties sessionProps, int minIdle, int maxPoolSize, String connectionTestQuery) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setDriverClassName(props.getDriverClassName());
        config.setMinimumIdle(minIdle);
        config.setMaximumPoolSize(maxPoolSize);
        config.setConnectionTestQuery(connectionTestQuery);
        config.setPoolName(poolName);

        if (sessionProps != null) {
            config.setDataSourceProperties(sessionProps);
        }

        return new HikariDataSource(config);
    }
}
