package com.jisj.winsearch.ado;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.Ole32;


/**
 * Static methods for ADO objects generation
 */
public class COMFactory {

    private COMFactory(){}

    /**
     * Create {@link ADOConnection}
     * @return new {@link ADOConnection} object
     */
    public static ADOConnection newADOConnection() {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        try {
            return factory.createObject(ADOConnection.class);
        } catch (COMException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create {@link ADORecordset}
     * @return new {@link ADORecordset} object
     */
    public static ADORecordset newADORecordSet() {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        try {
            return factory.createObject(ADORecordset.class);
        } catch (COMException ex) {
            throw new RuntimeException(ex);
        }
    }

}
