package org.mikesoft.winsearch.win;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.win.ObjectState.adStateClosed;
import static org.mikesoft.winsearch.win.ObjectState.adStateOpen;

class ADOFactoryTest {

    @Test
    void newConnection() {
        ADOConnection con = ADOFactory.newConnection();
        assertNotNull(con);
        assertEquals("Search.CollatorDSO.1", con.provider());
        assertEquals(adStateOpen.getValue(), con.state());
        con.Close();
        assertEquals(adStateClosed.getValue(), con.state());
    }

    @Test
    void newRecordSet() {
        ADORecordset rs = ADOFactory.newRecordSet();
        assertNotNull(rs);
        assertEquals(adStateClosed.getValue(), rs.state());
    }
}