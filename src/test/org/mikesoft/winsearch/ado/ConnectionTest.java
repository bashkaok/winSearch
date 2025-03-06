package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.OaIdlUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.ado.ConnectModeEnum.adModeUnknown;
import static org.mikesoft.winsearch.ado._Recordset.CursorTypeEnum.adOpenStatic;
import static org.mikesoft.winsearch.ado.ObjectState.adStateClosed;
import static org.mikesoft.winsearch.ado.ObjectState.adStateOpen;

class ConnectionTest {

    @Test
    void open_Search_CollatorDSO() {
        Connection con = COMFactory.newNativeSystemIndexConnection();
        assertNotNull(con);
        assertEquals("Search.CollatorDSO.1", con.getProvider());
        assertEquals(adStateOpen.getValue(), con.state());
        assertThrowsExactly(COMInvokeException.class, ()-> con.setMode(ConnectModeEnum.adModeRead));
        assertEquals(adModeUnknown.getValue(), con.mode());
        con.close();
        assertEquals(adStateClosed.getValue(), con.state());
    }


    @Test
    void execute() {
        Connection con = COMFactory.newNativeSystemIndexConnection();
        final String sql = """
                    SELECT System.ItemName, System.FileName, System.ItemNameDisplay
                    FROM SystemIndex
                    WHERE SCOPE='file:D:/downloads'
                    """;
        long count = 0;
        Recordset rs = con.execute(sql, count);
        long openCount = Arrays.stream((Object[][]) OaIdlUtil.toPrimitiveArray(rs.getRows(), true))
//                .map(item-> (String)item[0])
                .count();
        assertTrue(openCount > 0);
        rs.setCursorType(adOpenStatic);
        System.out.println("getRecordCount=" + rs.getRecordCount() + " : rsCount=" + openCount);
    }

}