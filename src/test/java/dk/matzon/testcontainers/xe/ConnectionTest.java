package dk.matzon.testcontainers.xe;

import oracle.jdbc.pool.OracleDataSource;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("ConstantConditions")
public class ConnectionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionTest.class);
    public static final String DOCKER_IMAGE_NAME = "gvenzl/oracle-xe:21.3.0-slim";
    public static final String INIT_SCRIPT = "init.sql";
    public static final String ORACLE_XE_STARTDB_MOUNTPOINT = "/container-entrypoint-startdb.d/init.sql";

    private static OracleContainer oracleContainer;

    @BeforeClass
    public static void beforeClass() throws Exception {
        oracleContainer = new OracleContainer(DOCKER_IMAGE_NAME)
                .withCopyFileToContainer(MountableFile.forClasspathResource(INIT_SCRIPT), ORACLE_XE_STARTDB_MOUNTPOINT);
        oracleContainer.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        oracleContainer.stop();
    }

    @Test
    public void testConnect() throws SQLException {
        String jdbcUrl = oracleContainer.getJdbcUrl();
        LOGGER.info("Initialized container with jdbcurl = {}", jdbcUrl);

        PoolDataSourceImpl poolDataSource = DataSourceBuilder.create()
                .type(PoolDataSourceImpl.class)
                .build();

        poolDataSource.setURL(jdbcUrl);
        poolDataSource.setConnectionFactoryClassName(OracleDataSource.class.getName());
        poolDataSource.setUser(oracleContainer.getUsername());
        poolDataSource.setPassword(oracleContainer.getPassword());

        JdbcTemplate jdbcTemplate = new JdbcTemplate(poolDataSource);
        Integer rowCount = jdbcTemplate.queryForObject("select count(1) from ConnectionTest", Integer.class);

        assertEquals("Expected to find 2 rows in table", 2, (int) rowCount);
    }
}
