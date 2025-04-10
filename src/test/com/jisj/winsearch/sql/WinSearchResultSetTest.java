package com.jisj.winsearch.sql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.jisj.winsearch.ado.ADOConnection;
import com.jisj.winsearch.ado.ADORecordset;
import com.jisj.winsearch.ado.ObjectStateEnum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static com.jisj.winsearch.ado.ObjectStateEnum.adStateClosed;

class WinSearchResultSetTest {
    static Connection con;

    static final String System_FileName = "standby.png";
    static final String System_FileExtension = ".png";
    static final String System_ItemPathDisplay = "D:\\Tools\\Java\\winSearch\\src\\test\\resources\\test-data\\standby.png";
    static final Date System_ItemDate = new Date(1741275729000L);
    static final int System_Image_VerticalSize = 230;


    @BeforeAll
    static void setUp() throws SQLException {
        con = new WinSearchDataSource().getConnection();
    }

    static WinSearchResultSet getOneRecordResultSet() throws SQLException {
        final String sqlOne = """
                    SELECT  System.FileName,
                            System.FileExtension,
                            System.ItemPathDisplay,
                            System.ItemDate,
                            System.Image.VerticalSize
                    FROM SystemIndex
                    WHERE DIRECTORY='file:D:/Tools/Java/winSearch/src/test/resources/test-data' AND CONTAINS(*,'standby.png')
                    """;

        /* Query results
         * System.FileName              (String) standby.png
         * System.FileExtension         (String) .png
         * System.ItemPathDisplay       (String) D:/Tools/Java/winSearch/src/test/resources/test-data/standby.png
         * System.ItemDate              (?DataTime) Thu Mar 06 18:42:09 MSK 2025,
         * System.Image.VerticalSize    (int) 230
         */
        return (WinSearchResultSet) con.createStatement().executeQuery(sqlOne);
    }

    static WinSearchResultSet getAnyRecordsResultSet() throws SQLException {
        final String sqlOne = """
                    SELECT  System.FileName,
                            System.FileExtension,
                            System.ItemPathDisplay,
                            System.ItemDate,
                            System.Image.VerticalSize
                    FROM SystemIndex
                    WHERE SCOPE='file:D:/Tools/Java/winSearch/src/test/resources/test-data'
                    """;
        return (WinSearchResultSet) con.createStatement().executeQuery(sqlOne);
    }


    @Test
    void size() throws SQLException {
        WinSearchResultSet rs = getOneRecordResultSet();
        assertEquals(1, rs.size());
        rs.close();
        assertThrowsExactly(WinSearchSQLException.class, rs::size);
    }

    @Test
    void next() throws SQLException {
        //on empty ResultSet
        final String sql = "SELECT System.ItemName FROM SystemIndex WHERE SCOPE='file:E:/Directory_not_found'";
        ResultSet rsEmpty = con.createStatement().executeQuery(sql);
        assertEquals(0, ((WinSearchResultSet)rsEmpty).size());
        assertFalse(rsEmpty.next());

        //on one record ResultSet
        WinSearchResultSet rs = getOneRecordResultSet();
        assertEquals(1,rs.size());
        assertTrue(rs.next());

        //on closed ResultSet
        rs.close();
        assertThrowsExactly(WinSearchSQLException.class, rs::next);
    }

    @Test
    void last() {
        //The problem with move(num) - only one record step
/*
        WinSearchResultSet rs = getAnyRecordsResultSet();
        assertTrue(rs.isBeforeFirst());
        rs.unwrap(ADORecordset.class).setCacheSize(10);
        while(rs.next()) {
            System.out.println(rs.getString(0));
            System.out.println(rs.getRow());
        }
        assertTrue(rs.size()>1);
        rs.beforeFirst();
        assertTrue(rs.isBeforeFirst());
        System.out.println(rs.unwrap(ADORecordset.class).cacheSize());

        rs.unwrap(ADORecordset.class).moveFirst();
        rs.unwrap(ADORecordset.class).move(2);
        rs.last();
        System.out.println(rs.size());
        System.out.println(rs.getRow());
        assertTrue(rs.last());
        assertEquals(rs.size(), rs.getRow());
*/
    }

    @Test
    void getObject() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertThrowsExactly(WinSearchSQLException.class, ()->rs.getObject(0));
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
            assertThrowsExactly(WinSearchSQLException.class, ()->rs.getObject(5));
        }
    }

    @Test
    void getString() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertThrowsExactly(WinSearchSQLException.class, ()->rs.getObject(0));
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
            assertThrowsExactly(WinSearchSQLException.class, ()->rs.getString(5));
        }

    }

    @Test
    void getDate() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertThrowsExactly(WinSearchSQLException.class, ()->rs.getObject(0));
        while (rs.next()) {
            assertEquals(System_ItemDate, rs.getDate(3));
            assertThrowsExactly(IllegalStateException.class, ()-> rs.getString(3));
            assertThrowsExactly(IllegalStateException.class, ()-> rs.getString(4));
            assertThrowsExactly(WinSearchSQLException.class, ()->rs.getString(5));
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

    private void printFlags(ResultSet rs) throws SQLException {
        System.out.println("isBeforeFist="+rs.isBeforeFirst());
    }

    @Test
    void getOptional() throws SQLException {
        WinSearchResultSet rs = getOneRecordResultSet();
        assertThrowsExactly(WinSearchSQLException.class, ()->rs.getObject(0));
        printFlags(rs);
        while (rs.next()) {
            printFlags(rs);
            assertTrue(rs.getOptional(0).isPresent());
            assertInstanceOf(String.class, rs.getOptional(0).get());
            assertTrue(rs.getOptional(3).isPresent());
            assertInstanceOf(Date.class, rs.getOptional(3).get());
            assertTrue(rs.getOptional(4).isPresent());
            assertInstanceOf(Integer.class, rs.getOptional(4).get());
            assertThrowsExactly(WinSearchSQLException.class, ()->rs.getObject(5));
            assertTrue(rs.getOptional(5).isEmpty());
        }
    }

        @Test
    void unwrap() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertInstanceOf(ADORecordset.class, rs.unwrap(ADORecordset.class));
        assertThrowsExactly(WinSearchSQLException.class, ()-> rs.unwrap(ADOConnection.class));
    }

    @Test
    void isWrapperFor() throws SQLException {
        ResultSet rs = getOneRecordResultSet();
        assertTrue(rs.isWrapperFor(ADORecordset.class));
        assertFalse(rs.isWrapperFor(ADOConnection.class));
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
        assertThrowsExactly(WinSearchSQLException.class, rs::beforeFirst);
        assertThrowsExactly(WinSearchSQLException.class, rs::isBeforeFirst);
    }

    @Test
    void isEmpty() throws SQLException {
        WinSearchResultSet rs = getOneRecordResultSet();
        assertFalse(rs.isEmpty());
        rs.close();
        assertThrowsExactly(WinSearchSQLException.class, rs::isEmpty);
        rs.close();
    }

    @Test
    void stream() throws SQLException {
        WinSearchResultSet rs = getOneRecordResultSet();
        assertFalse(rs.isEmpty());
        //Reusable RecordSet
        assertEquals(1, rs.stream().count());
        assertTrue(rs.isBeforeFirst());
        //Close Statement after use
        Statement statement = rs.getStatement();
        ADORecordset adoRs = rs.unwrap(ADORecordset.class);
        assertEquals(1, rs.stream(statement).count());
        assertTrue(rs.isClosed()); // ResultSet closed
        assertEquals(adStateClosed, ObjectStateEnum.valueOf(adoRs.state())); // ADORecordset closed
        assertTrue(statement.isClosed()); //Statement closed
        //Empty Stream after close
        assertEquals(0, rs.stream().count());
    }
}