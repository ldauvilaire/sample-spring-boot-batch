package net.ldauvilaire.sample.batch.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.MetaDataAccessException;

public class SampleBatchDatabaseInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleBatchDatabaseInitializer.class);

	private BatchProperties properties;
	private DataSource dataSource;
	private ResourceLoader resourceLoader;

	public SampleBatchDatabaseInitializer(
			BatchProperties properties,
			DataSource dataSource,
			ResourceLoader resourceLoader) {

		this.properties = properties;
		this.dataSource = dataSource;
		this.resourceLoader = resourceLoader;
	}

	public void initialize() {
		if (this.properties.getInitializer().isEnabled()) {
			LOGGER.info("*** Initializer Enabled ***");
			String platform = getDatabaseType();
			if ("hsql".equals(platform)) {
				platform = "hsqldb";
			}
			if ("postgres".equals(platform)) {
				platform = "postgresql";
			}
			if ("oracle".equals(platform)) {
				platform = "oracle10g";
			}
			LOGGER.info("*** Platform = [{}] ***", platform);
			ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			String schemaLocation = this.properties.getSchema();
			schemaLocation = schemaLocation.replace("@@platform@@", platform);
			LOGGER.info("*** SchemaLocation = [{}] ***", schemaLocation);
			populator.addScript(this.resourceLoader.getResource(schemaLocation));
			populator.setContinueOnError(true);
			LOGGER.info("*** Populating Db - Debut ***");
			DatabasePopulatorUtils.execute(populator, this.dataSource);
			LOGGER.info("*** Populating Db - Fin ***");
		} else {
			LOGGER.info("*** Initializer not Enabled ***");
		}
	}

	protected String getDatabaseType() {
		try {
			return DatabaseType.fromMetaData(this.dataSource).toString().toLowerCase();
		}
		catch (MetaDataAccessException ex) {
			throw new IllegalStateException("Unable to detect database type", ex);
		}
	}
}
