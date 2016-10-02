package net.ldauvilaire.sample.batch.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);

	@Bean(name="jobRepository")
	public JobRepository jobRepository(
			@Qualifier("techDataSource") DataSource dataSource,
			@Qualifier("techTransactionManager") PlatformTransactionManager transactionManager) throws Exception {

		LOGGER.info("Creating jobRepository ...");
		JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
		{
			factoryBean.setDataSource(dataSource);
			factoryBean.setTransactionManager(transactionManager);
		}
		return factoryBean.getObject();
	}

	@Bean(name="jobLauncher")
	public JobLauncher jobLauncher(@Qualifier("jobRepository") JobRepository jobRepository) {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}
}
