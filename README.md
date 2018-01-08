Spring JDBC Template

 

-   is a simplification in handling database access

-   has the following advantages compared with standard JDBC

    -   allows to clean-up the resources automatically(eg. release the database
        connection)

    -   converts the standard JDBC SQLException into RuntimeException

    -   converts the vendor specific error messages into better understandable
        error messages

-   offers several ways to query the database - queryForList() returns a list of
    HashMaps; the name of the column is the key in the hash map for the values
    in the table

-   ResultSetExtractor or RowMapper allows to translate the SQL result direct
    into an object(ResultSetExtractor) or a list of objects(RowMapper)

-   executes SQL queries, update statements and stored procedure calls, performs
    iteration over ResultSets and extraction of returned parameter values

-   handles the creation and release of resources, thus avoiding errors such as
    forgetting to close the connection

-   catches JDBC exceptions and translates them to the generic, more
    informative, exception hierarchy defined in the `org.springframework.dao`
    package

 

Typically needs lesser number of lines compared to JDBC as following are
simplified

-   mapping parameters to queries

-   liquidating resultsets to beans

-   zero exception handling needed because all exceptions are converted to
    RuntimeExceptions.

 

 

JdbcTemplate

-   provided by Spring

-   is a abstraction on top of JDBC API

-   provides great transaction management capabilities using annotation based
    approach

-   Spring Jdbc Template without Spring Boot requires registering
    **DataSource**, **TransactionManager** and **JdbcTemplate** beans and
    optionally we can register **DataSourceInitializer** bean to initialize our
    database

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
@Configuration
@ComponentScan
@EnableTransactionManagement
@PropertySource(value = { "classpath:application.properties" })
public class AppConfig 
{
    @Autowired
    private Environment env;

    @Value("${init-db:false}")
    private String initDatabase;
     
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }    

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public DataSource dataSource()
    {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.username"));
        dataSource.setPassword(env.getProperty("jdbc.password"));
        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource)
    {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();    
        dataSourceInitializer.setDataSource(dataSource);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("data.sql"));
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        dataSourceInitializer.setEnabled(Boolean.parseBoolean(initDatabase));
        return dataSourceInitializer;
    }
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

With this configuration in place ,we can inject JdbcTemplate into Data Access
components to interact with databases.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class User
{
    private Integer id;
    private String name;
    private String email;

    // setters & getters
}

@Repository
public class UserRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly=true)
    public List<User> findAll() {
        return jdbcTemplate.query("select * from users", new UserRowMapper());
    }
}

class UserRowMapper implements RowMapper<User>
{
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException 
    {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));

        return user;
    }
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

Using **SpringBoot** we can use **JdbcTemplate** without requiring to configure
all these beans manually. For this we must use the **spring-boot-starter-jdbc**
module.

-   the spring-boot-starter-jdbc module transitively pulls
    tomcat-jdbc-{version}.jar which is used to **configure the DataSource** bean

-   If you have not defined any **DataSource bean explicitly** and if you have
    any embedded database driver in classpath such as H2, HSQL or Derby then
    SpringBoot will automatically registers DataSource bean using in-memory
    database settings

-   If you haven’t registered any of the following type beans then SpringBoot
    will register them automatically.

    -   **PlatformTransactionManager** (**DataSourceTransacrionManager**)

    -   **JdbcTemplate**

    -   **NamedParameterJdbcTemplate**

-   SpringBoot uses schema.sql and data.sql to  automatically initialize
    database; additionally we can use schema-\${platform}.sql and
    data-\${platform}.sql; the \${platform} value is set in property
    spring.datasource.platform and can be hsqldb,h2,oracle,mysql,postgresql...

-   default database script names cand be customized using
    spring.datasource.schema=create-db.sql and
    spring.datasource.data=seed-data.sql

-   SpringBoot uses spring.datasource.initialize property value, default TRUE,
    to determine whether to initialize or not; if you want to turn off the
    database initialization you can set this property to FALSE

-   if the database script has errors, application will fail to start; if you
    still want to start you must ignore them using
    **spring.datasource.continueOnError=true**

-   you can inject JdbcTemplate into Repository

-   By default SpringBoot features such as external properties, logging etc are
    available in the ApplicationContext only if you use **SpringApplication**.
    So, SpringBoot provides **\@SpringApplicationConfiguration** annotation to
    configure the ApplicationContext for tests which
    uses **SpringApplication **behind the scenes.

-   if we want to use non-embedded RDBMSs we can configure the database
    properties in application.properties

-   if we want to have more control and configure **DataSource **bean by
    ourselves then we can configure DataSource bean in a Configuration class

-   if we register DataSource bean then SpringBoot will not configure DataSource
    automatically using AutoConfiguration

-   if we want to use another connection pooling (default is
    tomcat-jdbc-{version}.jar which uses org.apache.tomcat.jdbc.pool.DataSource
    to configure DataSource bean)

-   we must exclude in pom.xml the one that we don’t want; we can chose from the
    others remaining in the classpath:

    -   org.apache.tomcat.jdbc.pool.DataSource

    -   com.zaxxer.hikari.HikariDataSource

    -   org.apache.commons.dbcp.BasicDataSource

    -   org.apache.commons.dbcp2.BasicDataSource

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <exclusions>
        <exclusion>
        <groupId>org.apache.tomcat</groupId>
        <artifactId>tomcat-jdbc</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
</dependency>
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
