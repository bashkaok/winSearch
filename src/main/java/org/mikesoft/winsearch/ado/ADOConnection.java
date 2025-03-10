package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.COM.util.IConnectionPoint;
import com.sun.jna.platform.win32.COM.util.IUnknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.COM.COMInvokeException;

/**
 * A Connection object represents a unique session with a data source
 * <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/connection-object-ado?view=sql-server-ver15">MS Learn</a>
 * <p>
 * Mapped interface of ADODB.Connection.6.0 (C:\Program Files\Common Files\System\ado\msado15.dll)<br>
 * guid({00000514-0000-0010-8000-00AA006D2EA4})
 */

@ComObject(clsId = "{00000514-0000-0010-8000-00AA006D2EA4}", progId = "{B691E011-1797-432E-907A-4D8C69339129}")
public interface ADOConnection extends _Connection,
        IConnectionPoint,
        IUnknown {

}

/**
 * Mapped proxy interface _Connection v.6.1 (clsId = {00001550-0000-0010-8000-00AA006D2EA4})
 */
@SuppressWarnings("unused")
@ComInterface(iid = "{00001550-0000-0010-8000-00AA006D2EA4}")
interface _Connection {

    @ComProperty(name = "ConnectionString")
    String getConnectionString();

    /**
     * Gets open mode value.
     *
     * @return long value of {@link ConnectModeEnum}
     */
    @ComProperty(name = "Mode")
    long mode();

    /**
     * Sets open mode
     *
     * @param mode {@link ConnectModeEnum}
     * @throws COMInvokeException when connection is opened
     */
    @ComProperty(name = "Mode")
    void setMode(ConnectModeEnum mode);

    /**
     * Indicates for all applicable objects whether the state of the object is open or closed
     *
     * @return long value of {@link ObjectStateEnum}
     */

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
     * Executes specified SQL query
     *
     * @param sql   SQL statement
     * @param count returns the number of records that the operation affected (for update and insert queries)
     * @return {@link ADORecordset}
     * @see <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/execute-method-ado-connection?view=sql-server-ver15">Execute Method (ADO Connection)</a>
     */
    @ComMethod(name = "Execute")
    ADORecordset execute(String sql, long count);

    /**
     * Overloaded {@link #execute(String, long)}
     *
     * @param sql SQL statement
     * @return @return {@link ADORecordset}
     */
    @ComMethod(name = "Execute")
    ADORecordset execute(String sql);

    /**
     * <p>
     * memberId(10)</p>
     */
    @ComMethod(name = "Open")
    void open(String connectionString,
              String UserID,
              String Password,
              int Options);

    @ComMethod(name = "Open")
    void open();
}

