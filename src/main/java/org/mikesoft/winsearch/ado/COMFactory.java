package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.Ole32;


public class COMFactory {
    public static final String CONNECTION_STR = "Provider=Search.CollatorDSO;Extended Properties='Application=Windows';";

    public static Connection newNativeConnection() {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        try {
            return factory.createObject(Connection.class);
        } catch (COMException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates and opens native COM ADO connection with Search.CollatorDSO provider
     */
    public static Connection newNativeSystemIndexConnection() {
        Connection connection = newNativeConnection();
        connection.open(CONNECTION_STR, "", "", -1);
        return connection;
    }

    public static Recordset newRecordSet() {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        try {
            return factory.createObject(Recordset.class);
        } catch (COMException ex) {
            throw new RuntimeException(ex);
        }
    }

}
