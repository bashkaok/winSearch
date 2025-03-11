package org.mikesoft.winsearch.sql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class WinSearchStatementTest {
    static Connection con;

    final String SQL = """
            SELECT  System.FileName,
                    System.FileExtension,
                    System.ItemPathDisplay,
                    System.ItemDate,
                    System.Image.VerticalSize
            FROM SystemIndex
            WHERE SCOPE='file:D:/Tools/Java/winSearch/src/test/resources/test-data'
            """;

    @BeforeAll
    static void setUp() throws SQLException {
        con = new WinSearchDataSource().getConnection();
    }


    @Test
    void getResultSet() throws SQLException {
        Statement st = con.createStatement();
        assertNull(st.getResultSet());
        assertNotNull(st.executeQuery(SQL));
        assertNotNull(st.getResultSet());
        st.close();
        assertThrowsExactly(WinSearchSQLException.class, st::getResultSet);
    }
}