package com.jisj.winsearch.ado;

import org.junit.jupiter.api.Test;
import com.jisj.winsearch.sql.WinSearchDataSource;

import static org.junit.jupiter.api.Assertions.*;
import static com.jisj.winsearch.ado.ObjectStateEnum.adStateClosed;
import static com.jisj.winsearch.ado.ObjectStateEnum.adStateOpen;

class COMFactoryTest {

    @Test
    void newADOConnection() {
        ADOConnection con = COMFactory.newADOConnection();
        assertNotNull(con);
        assertEquals("MSDASQL", con.getProvider());
        assertEquals(adStateClosed.getValue(), con.state());
    }

    @Test
    void newNativeSystemIndexConnection() {
        ADOConnection con = WinSearchDataSource.newADOConnection();
        assertNotNull(con);
        assertEquals("Search.CollatorDSO.1", con.getProvider());
        assertEquals(adStateOpen.getValue(), con.state());
        con.close();
        assertEquals(adStateClosed.getValue(), con.state());
    }

    @Test
    void newADORecordSet() {
        ADORecordset rs = COMFactory.newADORecordSet();
        assertNotNull(rs);
        assertEquals(adStateClosed.getValue(), rs.state());
    }
}