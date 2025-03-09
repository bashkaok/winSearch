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
                    WHERE SCOPE='file:D:/Tools/Java/winSearch/src/test/resources/test-data'
                    """;
        ADORecordset rs = COMFactory.newRecordSet();
        rs.open(sql, con, _Recordset.CursorTypeEnum.adOpenStatic, _Recordset.LockTypeEnum.adLockReadOnly, adCmdUnspecified);
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
        while(!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(1,count);
        rs.moveFirst();
        rs.move(-1);
        rs.move(1);
        count = 0;
        while(!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(1,count);

    }

    @Test
    void incorrectSQL() {
        final String sql = "SELECT System.ItemName FROM SystemIndex WHERE SCOPE='file:E:/downloads'";
        ADORecordset rs = COMFactory.newRecordSet();
        rs.open(sql, con);
        System.out.println(rs.isEOF());
        System.out.println(rs.isBOF());

    }
    @Test
    void oneRecordRecordSet() {
        ADORecordset rs = getOneRecordset();
        assertEquals(_Recordset.CursorTypeEnum.adOpenStatic, _Recordset.CursorTypeEnum.valueOf(rs.cursorType()));
        System.out.println(rs.lockType());
        System.out.println(rs.getRecordCount());
        rs.move(-1);
        assertTrue(rs.isBOF());
        int count = 0;
        rs.moveFirst();
        while(!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(1, count);

        rs.moveFirst();
        Object[][] record = (Object[][]) OaIdlUtil.toPrimitiveArray(rs.getRows(1), true);
        System.out.println((String) record[0][0]);
    }

    @Test
    void getRows() {
        ADORecordset rs = getOneRecordset();
        assertFalse(rs.isEOF());
        rs.getRows();
        assertTrue(rs.isEOF());
    }




}