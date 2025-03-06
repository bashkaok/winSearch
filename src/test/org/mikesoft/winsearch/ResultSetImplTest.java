package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.ado.Recordset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ResultSetImplTest {
    static Connection con = new ConnectionImpl();
    private static final String sql = """
                    SELECT System.ItemName, System.FileName, System.ItemNameDisplay
                    FROM SystemIndex
                    WHERE SCOPE='file:D:/downloads'
                    """;

    static ResultSetImpl getOneRecordResultSet() throws SQLException {
        final String sqlOne = """
                    SELECT  System.FileName,
                            System.FileExtension,
                            System.ItemPathDisplay,
                            System.ItemDate,
                            System.Image.VerticalSize
                    FROM SystemIndex
                    WHERE SCOPE='file:D:/Tools/Java/winSearch/src/test/resources/test-data'
                    """;

        return (ResultSetImpl) con.createStatement().executeQuery(sqlOne);
    }

    @Test
    void size() throws SQLException {
        ResultSetImpl rs = getOneRecordResultSet();
        assertEquals(1, rs.size());
        rs.close();
        assertThrowsExactly(SearchSQLException.class, rs::size);
    }

    @Test
    void next() throws SQLException {
        //on empty ResultSet
        final String sql = "SELECT System.ItemName FROM SystemIndex WHERE SCOPE='file:E:/Directory_not_found'";
        ResultSet rsEmpty = con.createStatement().executeQuery(sql);
        assertEquals(0, ((ResultSetImpl)rsEmpty).size());
        assertFalse(rsEmpty.next());

        //on one record ResultSet
        ResultSetImpl rs = getOneRecordResultSet();
        assertEquals(1,rs.size());
        assertTrue(rs.next());

        //on closed ResultSet
        rs.close();
        assertThrowsExactly(SearchSQLException.class, rs::next);
    }

    @Test
    void getObject() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertThrowsExactly(SearchSQLException.class, ()->rs.getObject(0));
        while (rs.next()) {
            assertNotNull(rs.getObject(0));
            System.out.println(rs.getObject(0));
            assertNotNull(rs.getObject(1));
            System.out.println(rs.getObject(1));
            assertNotNull(rs.getObject(2));
            System.out.println(rs.getObject(2));
            System.out.println(rs.getObject(3));
            System.out.println(rs.getObject(4));
            assertThrowsExactly(SearchSQLException.class, ()->rs.getObject(5));
        }
    }

    @Test
    void unwrap() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertInstanceOf(Recordset.class, rs.unwrap(Recordset.class));
        assertThrowsExactly(SearchSQLException.class, ()-> rs.unwrap(org.mikesoft.winsearch.ado.Connection.class));
    }

    @Test
    void isWrapperFor() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertTrue(rs.isWrapperFor(Recordset.class));
        assertFalse(rs.isWrapperFor(org.mikesoft.winsearch.ado.Connection.class));
    }

    @Test
    void isBeforeFirst() {
    }

    @Test
    void beforeFirst() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        rs.beforeFirst();
        assertTrue(rs.isBeforeFirst());
        rs.next();
        assertFalse(rs.isBeforeFirst());
        rs.close();
        assertThrowsExactly(SearchSQLException.class, rs::beforeFirst);
        assertThrowsExactly(SearchSQLException.class, rs::isBeforeFirst);
    }

    @Test
    void isEmpty() throws SQLException {
        ResultSetImpl rs = getOneRecordResultSet();
        assertFalse(rs.isEmpty());
        rs.close();
        assertThrowsExactly(SearchSQLException.class, rs::isEmpty);
        rs.close();
    }
}