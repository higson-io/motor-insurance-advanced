package pl.decerto.motorinsuranceadvanced.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@Configuration
@ConditionalOnProperty(value = "hyperon.persistence.database.recreate", havingValue = "true")
public class DBGmoInitializer {

	private final Logger log = LoggerFactory.getLogger(DBGmoInitializer.class);

	@Autowired
	@Qualifier("hyperonPersistenceDataSource")
	private DataSource dataSource;

	@Autowired
	private ResourceLoader resourceLoader;

	@PostConstruct
	public void recreateDB() throws Exception {
		log.info("Recreating DB with script: schema.sql");
		Resource resource = resourceLoader.getResource("classpath:/sql/schema.sql");
		ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
	}
}
