package org.mikesoft.winsearch.win;

import com.sun.jna.platform.win32.COM.util.IConnectionPoint;
import com.sun.jna.platform.win32.COM.util.IUnknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

/**
 * <p>
 * ProgID: ADODB.Connection.6.0 </p>
 * <p> guid({00000514-0000-0010-8000-00AA006D2EA4})</p>
 * C:\Program Files\Common Files\System\ado\msado15.dll
 * <p>
 * source(ConnectionEvents)</p>
 */
@ComObject(clsId = "{00000514-0000-0010-8000-00AA006D2EA4}", progId = "{B691E011-1797-432E-907A-4D8C69339129}")
public interface ADOConnection extends _ADOConnection,
        IConnectionPoint,
        IUnknown {

}

/**
 * <p>
 * guid({00001550-0000-0010-8000-00AA006D2EA4})</p>
 * <a href="https://learn.microsoft.com/en-us/sql/ado/reference/ado-api/connection-object-properties-methods-and-events?view=sql-server-ver16"> MSLearn</a>
 */
@ComInterface(iid = "{00001550-0000-0010-8000-00AA006D2EA4}")
interface _ADOConnection {

    @ComProperty(name = "State")
    long state();

    @ComProperty(name = "Provider")
    String provider();
    /**
     * <p>
     * memberId(5)</p>
     */
    @ComMethod(name = "Close")
    void Close();

    /**
     * <p>
     * memberId(10)</p>
     */
    @ComMethod(name = "Open")
    void Open(String ConnectionString,
              String UserID,
              String Password,
              int Options);

}
