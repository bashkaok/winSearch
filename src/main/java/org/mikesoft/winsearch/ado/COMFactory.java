package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.Ole32;


public class COMFactory {
    public static ADOConnection newNativeConnection() {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        try {
            return factory.createObject(ADOConnection.class);
        } catch (COMException ex) {
            throw new RuntimeException(ex);
        }
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
