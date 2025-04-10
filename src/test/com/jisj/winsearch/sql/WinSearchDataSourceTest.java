package com.jisj.winsearch.sql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class WinSearchDataSourceTest {
    private static WinSearchDataSource ds;
    @BeforeAll
    static void setUp() {
        ds = new WinSearchDataSource();
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