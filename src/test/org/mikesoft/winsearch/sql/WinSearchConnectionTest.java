package org.mikesoft.winsearch.sql;

import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.ado.ADOConnection;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class WinSearchConnectionTest {

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