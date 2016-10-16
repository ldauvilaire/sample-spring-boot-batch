package net.ldauvilaire.sample.batch.configuration;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class BatchDatabaseConfiguration {

	@Bean(name = "batchDataSource")
	@ConfigurationProperties(prefix="batch.datasource")
	public DataSource batchDataSource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}
}
