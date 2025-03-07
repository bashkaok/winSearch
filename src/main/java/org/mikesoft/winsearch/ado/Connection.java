package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.COM.util.IConnectionPoint;
import com.sun.jna.platform.win32.COM.util.IUnknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.COM.COMInvokeException;

/**
 * Mapped interface of ADODB.Connection.6.0 (C:\Program Files\Common Files\System\ado\msado15.dll)
 * <p> guid({00000514-0000-0010-8000-00AA006D2EA4})</p>
 * @see <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/connection-object-ado?view=sql-server-ver15">Connection Object (ADO)</a>
 */

@ComObject(clsId = "{00000514-0000-0010-8000-00AA006D2EA4}", progId = "{B691E011-1797-432E-907A-4D8C69339129}")
public interface Connection extends _Connection,
        IConnectionPoint,
        IUnknown {

}

/**
 * <p>
 * guid({00001550-0000-0010-8000-00AA006D2EA4})</p>
 */
@SuppressWarnings("unused")
@ComInterface(iid = "{00001550-0000-0010-8000-00AA006D2EA4}")
interface _Connection {

    @ComProperty(name = "ConnectionString")
    String getConnectionString();

    @ComProperty(name = "Mode")
    long mode();
    /**
     * @throws COMInvokeException when connection is opened
     */
    @ComProperty(name = "Mode")
    void setMode(ConnectModeEnum mode);

    @ComProperty(name = "State")
    long state();

    @ComProperty(name = "Provider")
    String getProvider();

    /**
     * <p>
     * memberId(5)</p>
     */
    @ComMethod(name = "Close")
    void close();

    /**
     * @param sql contains the SQL statement
     * @param count returns the number of records that the operation affected
     * @see <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/execute-method-ado-connection?view=sql-server-ver15">Execute Method (ADO Connection)</a>
     */
    @ComMethod(name = "Execute")
    Recordset execute(String sql, long count);

    @ComMethod(name = "Execute")
    Recordset execute(String sql);

    /**
     * <p>
     * memberId(10)</p>
     */
    @ComMethod(name = "Open")
    void open(String ConnectionString,
              String UserID,
              String Password,
              int Options);

    @ComMethod(name = "Open")
    void open();
}

/**
 * Specifies the available permissions for modifying data in a {@link Connection}, opening a <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/record-object-ado?view=sql-server-ver15">Record</a>
 * @see <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/connectmodeenum?view=sql-server-ver15">ConnectModeEnum</a>
 */
@SuppressWarnings("unused")
enum ConnectModeEnum {
    adModeRead(1),
    adModeReadWrite(3),
    adModeRecursive(0x400000),
    adModeShareDenyNone(16),
    adModeShareDenyRead(4),
    adModeShareDenyWrite(8),
    adModeShareExclusive(12),
    adModeUnknown(0),
    adModeWrite(2);

    private final long value;
    ConnectModeEnum(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
