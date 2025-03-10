package org.mikesoft.winsearch.utils;

import com.sun.jna.platform.win32.OaIdl;
import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.ado.*;
import org.mikesoft.winsearch.sql.WinSearchDataSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.ado.ADORecordset.adCmdUnspecified;

class OaIdlUtilTest {

    private static ADORecordset getOneRecordset() {
        final String sql = """
                SELECT  System.ItemName,
                        System.ItemPathDisplay,
                        System.Size,
                        System.FileAttributes
                FROM SystemIndex
                WHERE DIRECTORY='file:D:/Tools/Java/winSearch/src/test/resources/test-data' AND CONTAINS(*,'standby.png')
                """;
        ADOConnection con = WinSearchDataSource.newADOConnection();
        ADORecordset rs = COMFactory.newRecordSet();
        rs.open(sql, con, CursorTypeEnum.adOpenStatic, LockTypeEnum.adLockReadOnly, adCmdUnspecified);
        return rs;
    }


    @Test
    void toPrimitiveArray() {
        ADORecordset rs = getOneRecordset();
        assertEquals(1, rs.getRecordCount());
        OaIdl.SAFEARRAY ar = rs.getRows();
        Object[][] pa = (Object[][]) OaIdlUtil.toPrimitiveArray(ar, true);
        System.out.println(Arrays.toString(pa[0]));
    }

}