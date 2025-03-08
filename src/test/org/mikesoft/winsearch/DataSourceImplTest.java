package org.mikesoft.winsearch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.sql.DataSourceImpl;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceImplTest {
    private static DataSourceImpl ds;
    @BeforeAll
    static void setUp() {
        ds = new DataSourceImpl();
    }

    @Test
    void getConnection() throws SQLException {
        assertNotNull(ds.getConnection());
    }

    @Test
    void testGetConnection() {
    }

    @Test
    void getLogWriter() {
    }

    @Test
    void setLogWriter() {
    }

    @Test
    void setLoginTimeout() {
    }

    @Test
    void getLoginTimeout() {
    }

    @Test
    void getParentLogger() {
    }

    @Test
    void unwrap() {
    }

    @Test
    void isWrapperFor() {
    }
}