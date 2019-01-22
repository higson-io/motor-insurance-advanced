package pl.decerto.motorinsuranceadvanced.config;

import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import pl.decerto.hyperon.persistence.factory.HyperonPersistenceFactory;
import pl.decerto.hyperon.persistence.service.BundleService;
import pl.decerto.hyperon.runtime.core.HyperonEngine;
import pl.decerto.hyperon.runtime.core.HyperonEngineFactory;
import pl.decerto.hyperon.runtime.profiler.jdbc.proxy.DataSourceProxy;
import pl.decerto.hyperon.runtime.sql.DialectRegistry;
import pl.decerto.hyperon.runtime.sql.DialectTemplate;

@ComponentScan("pl.decerto.app")
@Configuration
public class HyperonIntegrationConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(HyperonIntegrationConfiguration.class);

	private final Environment env;

	private final Boolean hyperonDevMode;

	private final String hyperonDevUser;

	@Autowired
	public HyperonIntegrationConfiguration(Environment env, @Value("${hyperon.dev.mode}") Boolean hyperonDevMode,
		@Value("${hyperon.dev.user}") String hyperonDevUser) {
		this.env = env;
		this.hyperonDevMode = hyperonDevMode;
		this.hyperonDevUser = hyperonDevUser;
	}

	@Bean
	public DialectRegistry getDialectRegistry() {
		var registry = new DialectRegistry();
		registry.setDialect(env.getProperty("hyperon.database.dialect"));
		return registry;
	}

	@Bean
	public DialectTemplate getDialectTemplate() {
		return getDialectRegistry().create();
	}

	@Bean(destroyMethod = "close")
	public DataSource getDataSource() {
		var dataSource = new BasicDataSource();
		dataSource.setUsername(env.getProperty("hyperon.database.username"));
		dataSource.setPassword(env.getProperty("hyperon.database.password"));
		dataSource.setUrl(env.getProperty("hyperon.database.url"));
		dataSource.setInitialSize(4);
		dataSource.setMaxActive(8);
		dataSource.setDriverClassName(getDialectTemplate().getJdbcDriverClassName());
		return dataSource;
	}

	@Bean
	public DataSource getDataSourceProxy() {
		var proxy = new DataSourceProxy();
		proxy.setDataSource(getDataSource());
		return proxy;
	}

	@Bean(destroyMethod = "destroy")
	public HyperonEngineFactory getHyperonEngineFactory() {
		var hyperonEngineFactory = new HyperonEngineFactory();
		hyperonEngineFactory.setDataSource(getDataSourceProxy());

		if (BooleanUtils.toBoolean(hyperonDevMode)) {
			LOG.info("Hyperon factory set in developer mode!");
			hyperonEngineFactory.setDeveloperMode(true);
			var hyperonDevUser = Objects.requireNonNull(this.hyperonDevUser, "Hyperon dev user not supplied");
			hyperonEngineFactory.setUsername(hyperonDevUser);
		}
		return hyperonEngineFactory;
	}

	@Bean
	public HyperonEngine getHyperonEngine(HyperonEngineFactory hyperonEngineFactory) {
		return hyperonEngineFactory.create();
	}

	@Bean("gmoDataSource")
	public DataSource secondaryDataSource() {
		var dataSource = new BasicDataSource();
		dataSource.setUsername(env.getProperty("gmo.database.username"));
		dataSource.setPassword(env.getProperty("gmo.database.password"));
		dataSource.setUrl(env.getProperty("gmo.database.url"));
		dataSource.setInitialSize(4);
		dataSource.setMaxActive(8);
		dataSource.setDriverClassName(getDialectTemplate().getJdbcDriverClassName());

		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(secondaryDataSource());
	}

	@Bean
	public HyperonPersistenceFactory hyperonPersistenceFactory() {
		var factory = new HyperonPersistenceFactory();
		factory.setHyperonDataSource(getDataSource());
		factory.setDataSource(secondaryDataSource());
		factory.setAutoStartWatchers(false);
		factory.setDefaultProfile(env.getProperty("hyperon.profile"));
		factory.setGmoDbDialect(env.getProperty("gmo.database.dialect"));
		factory.setHiloSequenceName("gmo_seq");
		factory.setBundleTable("gmo_bundle");
		factory.setBundleColumn("bid");
		factory.setOwnerColumn("parentid");
		factory.setOwnerPropertyColumn("collname");

		return factory;
	}

	@Bean
	public BundleService bundleService() {
		return hyperonPersistenceFactory().create();
	}
}
