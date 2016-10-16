package net.ldauvilaire.sample.batch.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties(BatchProperties.class)
public class BatchConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);

	@Bean
	public BatchConfigurer batchConfigurer(
			@Qualifier("batchDataSource") DataSource dataSource) {
		return new DefaultBatchConfigurer(dataSource);
	}

	@Bean
	public SampleBatchDatabaseInitializer batchDatabaseInitializer(
			BatchProperties properties,
			@Qualifier("batchDataSource") DataSource dataSource,
			ResourceLoader resourceLoader) {

		SampleBatchDatabaseInitializer initializer = new SampleBatchDatabaseInitializer(
				properties,
				dataSource,
				resourceLoader);

		LOGGER.info("-- Init Batch Database -- Debut ----------------------------------------------------");
		initializer.initialize();
		LOGGER.info("-- Init Batch Database -- Fin ------------------------------------------------------");

		return initializer;
	}

	@Bean(name="batchTransactionManager")
	public PlatformTransactionManager batchTransactionManager(
			@Qualifier("batchDataSource") DataSource dataSource) {

		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource);
		return tm;
	}

	@Bean(name ="jobRepository")
	@Primary
	public JobRepository jobRepository(
			@Qualifier("batchDataSource") DataSource dataSource,
			@Qualifier("batchTransactionManager") PlatformTransactionManager transactionManager) throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTransactionManager(transactionManager);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean(name ="jobLauncher")
	@Primary
	public JobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
		simpleJobLauncher.setJobRepository(jobRepository);
		return simpleJobLauncher;
	}
}
