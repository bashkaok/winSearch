package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.ado.Connection;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionImplTest {

    @Test
    void isClosed() throws SQLException {
        ConnectionImpl savedCon;
        try (var con = new ConnectionImpl()) {
            assertFalse(con.isClosed());
            savedCon = con;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertTrue(savedCon.isClosed());
    }

    @Test
    void isReadOnly() {
        try (var con = new ConnectionImpl()) {
            assertTrue(con.isReadOnly());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void unwrap() {
        try (var con = new ConnectionImpl()) {
            Object obj = con.unwrap(Connection.class);
            assertInstanceOf(Connection.class, obj);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isWrapperFor() {
        try (var con = new ConnectionImpl()) {
            assertTrue(con.isWrapperFor(Connection.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}