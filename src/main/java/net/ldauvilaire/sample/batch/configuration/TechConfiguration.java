package net.ldauvilaire.sample.batch.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import net.ldauvilaire.sample.batch.configuration.properties.DatasourceProperty;
import net.ldauvilaire.sample.batch.configuration.properties.KeyValueProperty;

@Configuration
@EnableConfigurationProperties({ DatasourceProperty.class, KeyValueProperty.class })
@ConfigurationProperties(prefix="tech")
public class TechConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterConfiguration.class);

	@NestedConfigurationProperty
	DatasourceProperty datasource = new DatasourceProperty();

	@NestedConfigurationProperty
	List<KeyValueProperty> jpaProperties = new ArrayList<KeyValueProperty>();

	//-- Empty Constructor --
	public TechConfiguration() {
	}

	//-- Getter/Setter for datasource --
	public DatasourceProperty getDatasource() {
		return datasource;
	}
	public void setDatasource(DatasourceProperty datasource) {
		this.datasource = datasource;
	}

	//-- Getter/Setter for jpaProperties --
	public List<KeyValueProperty> getJpaProperties() {
		return jpaProperties;
	}
	public void setJpaProperties(List<KeyValueProperty> jpaProperties) {
		this.jpaProperties = jpaProperties;
	}

	@Bean(name="techDataSource")
	public DataSource techDataSource() {

		LOGGER.info("techDriverClass = [{}]", datasource.getDriverClassName());
		LOGGER.info("techUrl = [{}]", datasource.getUrl());
		LOGGER.info("techUsername = [{}]", datasource.getUsername());
		LOGGER.info("techPassword = [{}]", datasource.getPassword());

		return DataSourceBuilder.create()
			.driverClassName(datasource.getDriverClassName())
			.url(datasource.getUrl())
			.username(datasource.getUsername())
			.password(datasource.getPassword())
			.build();
	}

	@Bean(name="techEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean techEntityManagerFactory(
			@Qualifier("techDataSource") DataSource dataSource) {

		if (jpaProperties == null) {
			LOGGER.info("techJpaProperties is null");
		} else {
			int nbProperties = jpaProperties.size();
			for (int i=0; i<nbProperties; i++) {
				String key = jpaProperties.get(i).getKey();
				String value = jpaProperties.get(i).getValue();
				LOGGER.info("techJpaProperty[{}]: [{}] = [{}]", i, key, value);
			}
		}

		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		{
			entityManagerFactoryBean.setDataSource(dataSource);
			entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
			entityManagerFactoryBean.setPackagesToScan("org.springframework.batch.core");
			entityManagerFactoryBean.setPersistenceUnitName("tech");

			
			if (jpaProperties != null) {
				Properties properties = new Properties();
				for (KeyValueProperty jpaProperty : jpaProperties) {
					String key = jpaProperty.getKey();
					String value = jpaProperty.getValue();
					properties.setProperty(key, value);
				}
				entityManagerFactoryBean.setJpaProperties(properties);
			}
		}

		return entityManagerFactoryBean;
	}

	@Bean(name="techTransactionManager")
	public PlatformTransactionManager techTransactionManager(@Qualifier("techEntityManagerFactory") EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
}
