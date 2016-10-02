package net.ldauvilaire.sample.batch.configuration;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import net.ldauvilaire.sample.batch.configuration.properties.DatasourceProperty;
import net.ldauvilaire.sample.batch.configuration.properties.KeyValueProperty;
import net.ldauvilaire.sample.batch.domain.model.Person;

@Configuration
@EnableConfigurationProperties({ DatasourceProperty.class, KeyValueProperty.class })
@ConfigurationProperties(prefix="master")
public class MasterConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterConfiguration.class);

	@NestedConfigurationProperty
	DatasourceProperty datasource = new DatasourceProperty();

	@NestedConfigurationProperty
	List<KeyValueProperty> jpaProperties = new ArrayList<KeyValueProperty>();

	//-- Empty Constructor --
	public MasterConfiguration() {
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

	@Bean(name="masterDataSource", destroyMethod="close")
	@Primary
	public DataSource datasource(DataSourceProperties properties) throws IllegalStateException, PropertyVetoException {

		LOGGER.info("masterDriverClass = [{}]", datasource.getDriverClassName());
		LOGGER.info("masterUrl = [{}]", datasource.getUrl());
		LOGGER.info("masterUsername = [{}]", datasource.getUsername());
		LOGGER.info("masterPassword = [{}]", datasource.getPassword());
		LOGGER.info("masterPoolSize = [{}]", datasource.getMaxPoolSize());

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		{
			dataSource.setDriverClass(datasource.getDriverClassName());
			dataSource.setJdbcUrl(datasource.getUrl());
			dataSource.setUser(datasource.getUsername());
			dataSource.setPassword(datasource.getPassword());
			if (datasource.getMaxPoolSize() != null) {
				dataSource.setMaxPoolSize(datasource.getMaxPoolSize().intValue());
			}
		}

		return dataSource;
	}

	@Bean(name="masterEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("masterDataSource") DataSource dataSource) {

		if (jpaProperties == null) {
			LOGGER.info("masterJpaProperties is null");
		} else {
			int nbProperties = jpaProperties.size();
			for (int i=0; i<nbProperties; i++) {
				String key = jpaProperties.get(i).getKey();
				String value = jpaProperties.get(i).getValue();
				LOGGER.info("masterJpaProperties[{}]: [{}] = [{}]", i, key, value);
			}
		}

		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		{
			entityManagerFactoryBean.setDataSource(dataSource);
			entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
			entityManagerFactoryBean.setPackagesToScan(Person.class.getPackage().getName());
			entityManagerFactoryBean.setPersistenceUnitName("master");
			
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

	@Bean(name="masterTransactionManager")
	@Primary
	public JpaTransactionManager transactionManager(@Qualifier("masterEntityManagerFactory") EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
}
