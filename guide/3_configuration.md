<h1>Connecting Application via Spring Annotations</h1>
We will show in few steps how to configure your application to work with Hyperon Peristence mechanism.

<h3>Gradle configuration</h3>
<span>Code example below only contains parts related to Hyperon</span>
<code>

    dependencies {
        // add code generation plugin for Hyperon Persistence
        classpath group: 'pl.decerto.hyperon.persistence', name: 'codegen-gradle-plugin', version:  '0.1.35'
    }

    apply plugin: 'codegen-gradle-plugin'

    sourceSets {
        hyperonGenerated {
            compileClasspath = configurations.hyperonGeneratedCompile
            java {
                srcDir file("generated-sources")
            }
        }
        main {
            compileClasspath += hyperonGenerated.output
            runtimeClasspath += hyperonGenerated.output
        }
        test {
            compileClasspath += hyperonGenerated.output
            runtimeClasspath += hyperonGenerated.output
        }

    }

    // configuration for codegen plugin
    bundle2java {
        bundleDefPath = "$projectDir/src/main/resources/bundle.def"
        generationPath = "$projectDir/generated-sources".toString()
        entitiesPackage = 'pl.decerto.app.model'
        servicePackage = 'pl.decerto.app.service'
        setterChaining = true
    }

    def hyperonVersion = '1.6.42'

    dependencies {
        compile sourceSets.hyperonGenerated.output
        api group: 'pl.decerto', name: 'hyperon-runtime', version: hyperonVersion
        api group: 'pl.decerto', name: 'hyperon-persistence', version: hyperonVersion

        hyperonGeneratedCompile group: 'pl.decerto', name: 'hyperon-persistence', version: hyperonVersion

        runtimeOnly('com.h2database:h2:1.4.196')
    }

    compileHyperonGeneratedJava.dependsOn tasks.getByName('bundle2java')
    classes.dependsOn hyperonGeneratedClasses
    compileJava.dependsOn compileHyperonGeneratedJava
</code>
<span>In above configuration there are two jar versions, that user can modify:</span>
<ul>
    <li>hyperonVersion - Hyperon Runtime and Persistence libraries version</li>
    <li>codegen-gradle-plugin version - this is separate version for code generation plugin, that works with hyperon-peristence</li>
</ul>
There is also <b>bundle2java</b> task, for code generation plugin. It will create required classes. Here are possible options:
<code>

    Required:
    - bundleDef - bundle definition represented as string
    - one of the following (exclusively):
      - generationPath - filesystem path in which Java files will be created (without package included)
      - entityWriter - custom entity writer
    Optional:
    - typesMapping - additional mapping of Hyperon types to Java classes, which overrides defaults
    - defaultPackage - package for the classes to be generated, the same both for entities and service, defaults to no package
    - entitiesPackage - package only for entities; if set, overrides default package value
    - servicePackage - package only for service; if set, overrides default package value
    - entitySuffix - suffix for each generated entity, apart from bundle root
    - bundleRootName - bundle root entity name, defaults to BundleRoot
    - serviceName - bundle service wrapper name, defaults to HyperonPersistenceService
    - setterChaining - allows setter methods chaining, defaults to disabled
    - skipServiceGeneration - skips service class generation, defaults to disabled
</code>
<h3>Spring configuration - bean setup for Hyperon Engine and Hyperon Persistence</h3>
Let's start with creating configuration class
<code>

    @ComponentScan("pl.decerto.app")
    @Configuration
    public class HyperonIntegrationConfiguration {

    }
</code>
In 
<code>
    @ComponentScan("...")
</code> 
put root package from your generated sources, in this example it is:
<i>pl.decerto.app</i>.
Next, let's create Hyperon Engine connected to Hyperon database
<code>

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
        return hyperonEngineFactory;
    }
</code>
Now Hyperon Persistence mechanism is configured.
<code>

	@Bean("hyperonPersistenceDataSource")
	public DataSource secondaryDataSource() {
        var dataSource = new BasicDataSource();
        dataSource.setUsername(env.getProperty("hyperon.persistence.database.username"));
        dataSource.setPassword(env.getProperty("hyperon.persistence.database.password"));
        dataSource.setUrl(env.getProperty("hyperon.persistence.database.url"));
        dataSource.setInitialSize(4);
        dataSource.setMaxActive(8);
        dataSource.setDriverClassName(getDialectTemplate().getJdbcDriverClassName());

        return dataSource;
	}

	@Bean
	public HyperonPersistenceFactory hyperonPersistenceFactory() {
        var factory = new HyperonPersistenceFactory();
        factory.setHyperonDataSource(getDataSource());
        factory.setDataSource(secondaryDataSource());
        factory.setAutoStartWatchers(false);
        factory.setDefaultProfile(env.getProperty("hyperon.profile"));
        factory.setGmoDbDialect(env.getProperty("hyperon.persistence.database.dialect"));
        factory.setHiloSequenceName("persistence_seq");
        factory.setBundleTable("persistence_bundle");
        factory.setBundleColumn("bid");
        factory.setOwnerColumn("parentid");
        factory.setOwnerPropertyColumn("collname");

        return factory;
	}

	@Bean
	public BundleService bundleService() {
        return hyperonPersistenceFactory().create();
	}
</code>
<b>HyperonPersistenceFactory</b>
 is factory helper class to create <b>BundleService</b>,
which is main facade used by developer to interact with Hyperon Persistence engine. As it is presented in code example,
<code>

    factory.setDataSource(secondaryDataSource());
</code> 
It uses second data source, which will be further called "business database".<br>
<b>BundleService</b> is created as Spring bean and it is injected into service called
<b>HyperonPersistenceService</b>, which was generated by code generation tool defined
in previous steps.
<h3>Resource files</h3>
Create file <i>application.yml</i> in project's resource directory. In this file put project configuration, like database connection
definition:

<code>

    server.port: 8081
    // this section is used by hyperon engine
    hyperon:
      database:
        url: jdbc:h2:c:/work/hyperon-studio-1.6.15/database/hyperon.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE
        username: sa
        password: sa
        dialect: h2
      dev:
        mode: false
        user: admin
      // this section is used by hyperon-peristence
      profile: DEMO
      persistence:
        database:
          url: jdbc:h2:./db/hyperon.persistence.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE
          username: sa
          password: sa
          dialect: h2
          recreate: false
</code>
Be sure to provide proper url for both databases.
Database for this example defined under <i>hyperon.persistence.database.url</i> - is available for download at
<a href="https://github.com/hyperon-io/motor-insurance-advanced/tree/master/db">github project</a>.

<i>hyperon.persistence.database.recreate</i> - is flag for creating database definition from prepared script:
available also at github. For this tutorial purpose, it can be <i>false</i> if structure was already created.

