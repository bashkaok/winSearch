package org.mikesoft.winsearch.win;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.Ole32;


public class ADOFactory {
    public static final String CONNECTION_STR = "Provider=Search.CollatorDSO;Extended Properties='Application=Windows';";

    public static ADOConnection newConnection() {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        final ADOConnection connection;
        try {
            connection = factory.createObject(ADOConnection.class);
        } catch (COMException ex) {
            throw new RuntimeException(ex);
        }
        connection.Open(CONNECTION_STR, "", "", -1);
        return connection;
    }

    public static ADORecordset newRecordSet() {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        try {
            return factory.createObject(ADORecordset.class);
        } catch (COMException ex) {
            throw new RuntimeException(ex);
        }
    }

}
