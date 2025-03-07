package org.mikesoft.winsearch.ado;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.ado.ObjectStateEnum.adStateClosed;
import static org.mikesoft.winsearch.ado.ObjectStateEnum.adStateOpen;

class COMFactoryTest {

    @Test
    void newNativeConnection() {
        Connection con = COMFactory.newNativeConnection();
        assertNotNull(con);
        assertEquals("MSDASQL", con.getProvider());
        assertEquals(adStateClosed.getValue(), con.state());
    }

    @Test
    void newNativeSystemIndexConnection() {
        Connection con = COMFactory.newNativeSystemIndexConnection();
        assertNotNull(con);
        assertEquals("Search.CollatorDSO.1", con.getProvider());
        assertEquals(adStateOpen.getValue(), con.state());
        con.close();
        assertEquals(adStateClosed.getValue(), con.state());
    }

    @Test
    void newRecordSet() {
        Recordset rs = COMFactory.newRecordSet();
        assertNotNull(rs);
        assertEquals(adStateClosed.getValue(), rs.state());
    }
}