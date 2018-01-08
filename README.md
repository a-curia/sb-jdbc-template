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

-   Spring Jdbc Template without Spring Boot requires registering DataSource,
    TransactionManager and JdbcTemplate beans and optionally we can register
    DataSourceInitializer bean to initialize our database
