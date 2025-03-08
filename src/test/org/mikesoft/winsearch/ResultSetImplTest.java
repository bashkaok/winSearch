package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.ado.Recordset;
import org.mikesoft.winsearch.sql.ConnectionImpl;
import org.mikesoft.winsearch.sql.ResultSetImpl;
import org.mikesoft.winsearch.sql.SearchSQLException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ResultSetImplTest {
    static Connection con = new ConnectionImpl();

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

        /* Query results
         * System.FileName              (String) standby.png
         * System.FileExtension         (String) .png
         * System.ItemPathDisplay       (String) D:/Tools/Java/winSearch/src/test/resources/test-data/standby.png
         * System.ItemDate              (?DataTime) Thu Mar 06 18:42:09 MSK 2025,
         * System.Image.VerticalSize    (int) 230
         */
        return (ResultSetImpl) con.createStatement().executeQuery(sqlOne);
    }

    static final String System_FileName = "standby.png";
    static final String System_FileExtension = ".png";
    static final String System_ItemPathDisplay = "D:\\Tools\\Java\\winSearch\\src\\test\\resources\\test-data\\standby.png";
    static final Date System_ItemDate = new Date(1741275729000L);
    static final int System_Image_VerticalSize = 230;

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
        //getObject(int columnIndex)
        while (rs.next()) {
            assertNotNull(rs.getObject(0));
            System.out.println(rs.getObject(0));
            assertNotNull(rs.getObject(1));
            System.out.println(rs.getObject(1));
            assertNotNull(rs.getObject(2));
            System.out.println(rs.getObject(2));
            System.out.println((rs.getObject(3)));
            System.out.println(rs.getObject(4));
            assertThrowsExactly(SearchSQLException.class, ()->rs.getObject(5));
        }
    }

    @Test
    void getString() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertThrowsExactly(SearchSQLException.class, ()->rs.getObject(0));
        //getString(int columnIndex)
        while (rs.next()) {
            assertNotNull(rs.getString(0));
            assertInstanceOf(String.class, rs.getString(0));
            assertEquals(System_FileName, rs.getString(0));
            assertNotNull(rs.getObject(1));
            assertEquals(System_FileExtension, rs.getObject(1));
            assertNotNull(rs.getObject(2));
            assertEquals(System_ItemPathDisplay, rs.getString(2));
            assertThrowsExactly(IllegalStateException.class, ()-> rs.getString(3));
            assertThrowsExactly(IllegalStateException.class, ()-> rs.getString(4));
            assertThrowsExactly(SearchSQLException.class, ()->rs.getString(5));
        }

    }

    @Test
    void getDate() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertThrowsExactly(SearchSQLException.class, ()->rs.getObject(0));
        while (rs.next()) {
            assertEquals(System_ItemDate, rs.getDate(3));
            assertThrowsExactly(IllegalStateException.class, ()-> rs.getString(3));
            assertThrowsExactly(IllegalStateException.class, ()-> rs.getString(4));
            assertThrowsExactly(SearchSQLException.class, ()->rs.getString(5));
        }
    }

    @Test
    void getInt() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        while (rs.next()) {
            assertEquals(System_Image_VerticalSize, rs.getInt(4));
            assertThrowsExactly(IllegalStateException.class, ()-> rs.getString(3));
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

    @Test
    void stream() throws SQLException {
        ResultSetImpl rs = getOneRecordResultSet();
        assertFalse(rs.isEmpty());
        assertEquals(1, rs.stream().count());
//        assertEquals(System_FileName, rs.stream().map(r-> r.getString(0)).findAny().orElseThrow());
    }
}