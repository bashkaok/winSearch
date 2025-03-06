package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.OaIdlUtil;
import org.junit.jupiter.api.*;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.COMInvokeException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.ado.Recordset.adCmdUnspecified;
import static org.mikesoft.winsearch.ado.ObjectState.adStateOpen;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecordsetTest {
    final static String sql = "SELECT System.ItemName, System.FileName, System.ItemNameDisplay FROM SystemIndex WHERE SCOPE='file:D:/downloads'";
    static Connection con;
    static Recordset rs;
    static long openCount;

    @BeforeAll
    static void setUp() {
        con = COMFactory.newNativeSystemIndexConnection();
        assertNotNull(con);
        rs = COMFactory.newRecordSet();
        assertNotNull(rs);
    }

    private static Recordset getOneRecordset() {
        final String sql = """
                    SELECT System.ItemName, System.FileName, System.ItemNameDisplay
                    FROM SystemIndex
                    WHERE SCOPE='file:D:/Tools/Java/winSearch/src/test/resources'
                    """;
        Recordset rs = COMFactory.newRecordSet();
        rs.open(sql, con, _Recordset.CursorTypeEnum.adOpenStatic, _Recordset.LockTypeEnum.adLockReadOnly, adCmdUnspecified);
        return rs;
    }

    @Test
    @Order(1)
    void open() {
        rs.open(sql, con, _Recordset.CursorTypeEnum.adOpenStatic, _Recordset.LockTypeEnum.adLockUnspecified, adCmdUnspecified);
        assertEquals(adStateOpen.getValue(), rs.state());
        assertFalse(rs.isEOF());
        OaIdl.SAFEARRAY array = rs.getRows();
        openCount = Arrays.stream((Object[][]) OaIdlUtil.toPrimitiveArray(array, true))
//                .map(item-> (String)item[0])
                .count();
        assertTrue(openCount > 0);
        assertEquals(rs.getRecordCount(), openCount);
        rs.moveFirst();
        Arrays.stream((Object[][]) OaIdlUtil.toPrimitiveArray(rs.getRows(), true))
                .forEach(item-> System.out.println((String)item[0]));
    }

    @Test
    @Order(2)
    void moveNext() {
        assertThrowsExactly(COMInvokeException.class, ()->rs.moveNext());
        rs.moveFirst();
        int count = 0;
        while (!rs.isEOF()) {
            rs.moveNext();
            count++;
        }
        assertEquals(openCount, count);
    }

    @Test
    @Order(3)
    void move() {
        rs.moveFirst();
        while(!rs.isEOF()) {
            rs.moveNext();
        }
        moveNext();
        try {
            rs.moveLast();
        } catch (COMInvokeException e) {
            System.out.println(e.getScode());
            e.printStackTrace();
        }
    }

    @Test
    void incorrectSQL() {
        final String sql = "SELECT System.ItemName FROM SystemIndex WHERE SCOPE='file:E:/downloads'";
        Recordset rs = COMFactory.newRecordSet();
        rs.open(sql, con);
        System.out.println(rs.isEOF());
        System.out.println(rs.isBOF());

    }
    @Test
    void oneRecordRecordSet() {
        Recordset rs = getOneRecordset();
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
        Recordset rs = getOneRecordset();
        assertFalse(rs.isEOF());
        rs.getRows();
        assertTrue(rs.isEOF());

        //try read before first record
        rs.moveFirst();
        rs.move(-1);
        rs.getRows(1);

    }




}