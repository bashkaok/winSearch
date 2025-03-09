package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.ado.ADOConnection;
import org.mikesoft.winsearch.sql.WinSearchConnection;
import org.mikesoft.winsearch.sql.WinSearchDataSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class WinSearchADOConnectionTest {

    @Test
    void isClosed() throws SQLException {
        WinSearchConnection savedCon;
        try (var con = new WinSearchDataSource().getConnection()) {
            assertFalse(con.isClosed());
            savedCon = (WinSearchConnection) con;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertTrue(savedCon.isClosed());
    }

    @Test
    void isReadOnly() {
        try (var con = new WinSearchDataSource().getConnection()) {
            assertTrue(con.isReadOnly());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void unwrap() {
        try (var con = new WinSearchDataSource().getConnection()) {
            Object obj = con.unwrap(ADOConnection.class);
            assertInstanceOf(ADOConnection.class, obj);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isWrapperFor() {
        try (var con = new WinSearchDataSource().getConnection()) {
            assertTrue(con.isWrapperFor(ADOConnection.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}