package org.mikesoft.winsearch.ado;


import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.OaIdl;

/**
 * Represents the entire set of records from a base table or the results of an executed command
 * <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/recordset-object-ado?view=sql-server-ver15">MS Learn</a>
 * <p>
 * Mapped interface of ADODB.Recordset.6.0 (clsId = {00000535-0000-0010-8000-00AA006D2EA4})
 */
@ComObject(clsId = "{00000535-0000-0010-8000-00AA006D2EA4}", progId = "{00000300-0000-0010-8000-00AA006D2EA4}")
public interface ADORecordset extends _Recordset {
    /**
     * Default value for unspecified values
     */
    int adCmdUnspecified = -1;
}

/**
 * Mapped proxy interface _Record v.6.1 (clsId = {00000556-0000-0010-8000-00AA006D2EA4})
 */
@ComInterface(iid = "{00000556-0000-0010-8000-00AA006D2EA4}")
interface _Recordset {

    /**
     * Indicates the ordinal position of a Recordset object's current record
     *
     * @return long record position or {@link PositionEnum PositionEnum} constant
     */
    @ComProperty(name = "AbsolutePosition")
    long absolutePosition();

    /**
     * Property to control how many records to retrieve at one time into local memory from the provider. Default is 1
     *
     * @return size of cache
     * @see #setCacheSize(long)
     */
    @ComProperty(name = "CacheSize")
    long cacheSize();

    /**
     * Sets cash size
     *
     * @param size Long value that must be greater than 0. Default is 1
     * @see #cacheSize()
     */
    @ComProperty(name = "CacheSize")
    void setCacheSize(long size);

    @ComProperty(name = "CursorType")
    long cursorType();

    @ComProperty(name = "CursorType")
    void setCursorType(CursorTypeEnum cursorType);

    @ComProperty(name = "LockType")
    long lockType();

    /**
     * How many records are in a Recordset object.
     *
     * @return -1 when ADO cannot determine the number of records or if the provider or cursor type does not support RecordCount.
     * @throws COMInvokeException reading the RecordCount property on a closed Recordset
     */
    @ComProperty(name = "RecordCount")
    long getRecordCount();

    /**
     * Indicates for all applicable objects whether the state of the object is open or closed
     * <p>
     * memberId(1006)
     *
     * @return long value of {@link ObjectStateEnum}
     */
    @ComProperty(name = "State")
    long state();

    @ComProperty(name = "Source")
    String getSource();

    /**
     * Returns true if the current record position is before the first record and false if the current record position is on or after the first record.
     */
    @ComProperty(name = "BOF")
    Boolean isBOF();

    /**
     * Indicates that the current record position is after the last record in a Recordset object
     */
    @ComProperty(name = "EOF")
    Boolean isEOF();

    /**
     * After you call GetRows, the next unread record becomes the current record, or the EOF property is set to True if there are no more records.
     * <p>memberId(1016)</p>
     */
    @ComMethod(name = "GetRows")
    OaIdl.SAFEARRAY getRows(int rows,
                            Object start,
                            Object fields);

    /**
     * After call of GetRows, the next unread record becomes the current record, or the EOF property is set to True if there are no more records
     *
     * @param rows value that indicates the number of records to retrieve
     * @throws COMInvokeException An attempt to move forward when the isEOF is true, or isBOF is true
     */
    @ComMethod(name = "GetRows")
    OaIdl.SAFEARRAY getRows(int rows) throws COMInvokeException;

    /**
     * Gets all records from recordset
     * @return two-dimensional array {@link OaIdl.SAFEARRAY SAFEARRAY}
     */
    //memberId(1016)</p>
    @ComMethod(name = "GetRows")
    OaIdl.SAFEARRAY getRows();

    /**
     * <p>
     * memberId(1014)</p>
     */
    @ComMethod(name = "Close")
    void close();

    @ComMethod(name = "Move")
    void move(long numRecords);

    @ComMethod(name = "MoveFirst")
    void moveFirst();

    @ComMethod(name = "MoveLast")
    void moveLast();

    /**
     * Moves the current record position one record forward
     *
     * @throws COMInvokeException An attempt to move forward when the isEOF is true
     */
    @ComMethod(name = "MoveNext")
    void moveNext();

    /**
     * Moves the current record position one record backward
     *
     * @throws COMInvokeException An attempt to move backward when the isBOF is true
     */
    @ComMethod(name = "MovePrevious")
    void movePrevious() throws COMInvokeException;

    /**
     * Opens a cursor on a Recordset object
     *
     * @param source           Optional. A Variant that evaluates to a valid Command object, a SQL statement, a table name, a stored procedure call, a URL, or the name of a file or Stream object containing a persistently stored Recordset.
     * @param activeConnection Optional. Either a Variant that evaluates to a valid {@link ADOConnection Connection} object variable name, or a {@link String} that contains {@link ADOConnection#getConnectionString() ConnectionString} parameters
     * @param CursorType       Optional. A {@link CursorTypeEnum CursorTypeEnum} value that determines the type of cursor that the provider should use when opening the {@link ADORecordset Recordset}. The default value is {@link CursorTypeEnum#adOpenForwardOnly adOpenForwardOnly}
     * @param LockType         LockType Optional. A {@link LockTypeEnum LockTypeEnum} value that determines what type of locking (concurrency) the provider should use when opening the Recordset. The default value is {@link LockTypeEnum#adLockReadOnly adLockReadOnly}
     * @param Options          Optional. A Long value that indicates how the provider should evaluate the Source argument if it represents something other than a Command object, or that the Recordset should be restored from a file where it was previously saved. Can be one or more CommandTypeEnum or ExecuteOptionEnum values, which can be combined with a bitwise OR operator
     */
    @ComMethod(name = "Open")
    void open(Object source,
              Object activeConnection,
              CursorTypeEnum CursorType,
              LockTypeEnum LockType,
              int Options);


}

