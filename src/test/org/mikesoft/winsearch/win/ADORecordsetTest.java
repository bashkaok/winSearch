package org.mikesoft.winsearch.win;

import com.sun.jna.platform.win32.OaIdlUtil;
import org.junit.jupiter.api.*;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.COM.COMInvokeException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.win.ADORecordset.adCmdUnspecified;
import static org.mikesoft.winsearch.win.ObjectState.adStateOpen;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ADORecordsetTest {
    final static String sql = "SELECT System.ItemName, System.FileName, System.ItemNameDisplay FROM SystemIndex WHERE SCOPE='file:D:/downloads'";
    static ADOConnection con;
    static ADORecordset rs;
    static long openCount;

    @BeforeAll
    static void setUp() {
        con = ADOFactory.newConnection();
        assertNotNull(con);
        rs = ADOFactory.newRecordSet();
        assertNotNull(rs);
    }

    @Test
    @Order(1)
    void open() {
        rs.open(sql, con, CursorTypeEnum.adOpenUnspecified, LockTypeEnum.adLockUnspecified, adCmdUnspecified);
        assertEquals(adStateOpen.getValue(), rs.state());
        assertFalse(rs.isEOF());
        OaIdl.SAFEARRAY array = rs.getRows();
        openCount = Arrays.stream((Object[][]) OaIdlUtil.toPrimitiveArray(array, true))
//                .map(item-> (String)item[0])
                .count();
        assertTrue(openCount > 0);
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

}