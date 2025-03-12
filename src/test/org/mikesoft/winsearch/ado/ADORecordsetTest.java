package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.OaIdlUtil;
import org.junit.jupiter.api.*;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.COMInvokeException;
import org.mikesoft.winsearch.sql.WinSearchDataSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.ado.ADORecordset.adCmdUnspecified;
import static org.mikesoft.winsearch.ado.ObjectStateEnum.adStateOpen;
import static org.mikesoft.winsearch.ado.CursorTypeEnum.adOpenStatic;
import static org.mikesoft.winsearch.ado.LockTypeEnum.adLockReadOnly;

class ADORecordsetTest {
    static ADOConnection con;

    @BeforeAll
    static void setUp() {
        con = WinSearchDataSource.newADOConnection();
        assertNotNull(con);
    }

    private static ADORecordset getOneRecordset() {
        final String sql = """
                SELECT System.ItemName, System.FileName, System.ItemNameDisplay
                FROM SystemIndex
                WHERE DIRECTORY='file:D:/Tools/Java/winSearch/src/test/resources/test-data' AND CONTAINS(*,'standby.png')
                """;
        ADORecordset rs = COMFactory.newADORecordSet();
        rs.open(sql, con, adOpenStatic, adLockReadOnly, adCmdUnspecified);
        return rs;
    }

    private static ADORecordset getAnyRecordset() {
        final String sql = """
                SELECT System.ItemName, System.FileName, System.ItemNameDisplay
                FROM SystemIndex
                WHERE SCOPE='file:D:/Tools/Java/winSearch/src/test/resources/test-data'
                """;
        ADORecordset rs = COMFactory.newADORecordSet();
        rs.open(sql, con, adOpenStatic, adLockReadOnly, adCmdUnspecified);
        return rs;
    }

    @Test
    void open() {
        ADORecordset rs = getOneRecordset();
        assertEquals(adStateOpen.getValue(), rs.state());
        assertFalse(rs.isEOF());
        OaIdl.SAFEARRAY array = rs.getRows();
        long openCount = Arrays.stream((Object[][]) OaIdlUtil.toPrimitiveArray(array, true))
//                .map(item-> (String)item[0])
                .count();
        assertEquals(rs.getRecordCount(), openCount);
    }

    @Test
    void getRecordCount() {
        ADORecordset rs = getOneRecordset();
        assertEquals(1, rs.absolutePosition());
        assertEquals(1, rs.getRecordCount());
        assertEquals(1, rs.absolutePosition());

        rs = getAnyRecordset();
        assertEquals(1, rs.absolutePosition());
        assertTrue(rs.getRecordCount() > 1);
        assertEquals(1, rs.absolutePosition());

    }

    @Test
    void moveNext() {
        ADORecordset rs = getOneRecordset();
        int count = 0;
        while (!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(rs.getRecordCount(), count);
        assertThrowsExactly(COMInvokeException.class, rs::moveNext);
    }

    @Test
    void move() {
        ADORecordset rs = getOneRecordset();
        int count = 0;
        while (!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(1, count);
        rs.moveFirst();
        rs.move(-1);
        rs.move(1);
        count = 0;
        while (!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(1, count);

    }

    @Test
    void moveFirst() {
        ADORecordset rs = getOneRecordset();
//        System.out.println("BOF=" + rs.isBOF() + " : EOF=" + rs.isEOF() + " : absolutePosition=" + rs.absolutePosition());
        rs.moveNext();
        assertEquals(PositionEnum.adPosEOF.getValue(), rs.absolutePosition());
        rs.moveFirst();
        assertEquals(1, rs.absolutePosition());
    }

    @Test
    void movePrevious() {
        ADORecordset rs = getAnyRecordset();
        assertTrue(!rs.isBOF() && !rs.isEOF());
//        System.out.println("BOF=" + rs.isBOF() + " : EOF=" + rs.isEOF() + " : absolutePosition=" + rs.absolutePosition());
        rs.movePrevious();
//        System.out.println("BOF=" + rs.isBOF() + " : EOF=" + rs.isEOF() + " : absolutePosition=" + rs.absolutePosition());
        assertTrue(rs.isBOF());
        rs.moveNext();
        rs.moveNext();
        rs.moveNext();
//        System.out.println("BOF=" + rs.isBOF() + " : EOF=" + rs.isEOF() + " : absolutePosition=" + rs.absolutePosition());
    }


    @Test
    void incorrectSQL() {
        final String sql = "SELECT System.ItemName FROM SystemIndex WHERE SCOPE='file:E:/downloads'";
        ADORecordset rs = COMFactory.newADORecordSet();
        rs.open(sql, con, adOpenStatic, adLockReadOnly, adCmdUnspecified);
        assertTrue(rs.isEOF() && rs.isBOF());

    }

    @Test
    void oneRecordRecordSet() {
        ADORecordset rs = getOneRecordset();
        assertEquals(adOpenStatic, CursorTypeEnum.valueOf(rs.cursorType()));
        rs.move(-1);
        assertTrue(rs.isBOF());
        int count = 0;
        rs.moveFirst();
        while (!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(1, count);

        rs.moveFirst();
        Object[][] record = (Object[][]) OaIdlUtil.toPrimitiveArray(rs.getRows(1), true);
    }

    @Test
    void getRows() {
        ADORecordset rs = getOneRecordset();
        assertFalse(rs.isEOF());
        rs.getRows();
        assertTrue(rs.isEOF());
        assertThrowsExactly(COMInvokeException.class, () -> rs.getRows(1));
    }


}