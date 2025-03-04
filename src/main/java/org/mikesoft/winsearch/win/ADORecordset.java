package org.mikesoft.winsearch.win;


import com.sun.jna.platform.win32.COM.util.IComEnum;
import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.OaIdl;

/**
 * @see <a href=https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/recordset-object-ado?view=sql-server-ver15>Recordset Object (ADO)</a>
 * <p>
 * guid({00000535-0000-0010-8000-00AA006D2EA4}) msado15.dll</p>
 */
@ComObject(clsId = "{00000535-0000-0010-8000-00AA006D2EA4}", progId = "{00000300-0000-0010-8000-00AA006D2EA4}")
public interface ADORecordset extends _Recordset {
    int adCmdUnspecified = -1;
}

/**
 *
 *
 * <p>
 * guid({00000556-0000-0010-8000-00AA006D2EA4})</p>
 */
@ComInterface(iid = "{00000556-0000-0010-8000-00AA006D2EA4}")
interface _Recordset {

    /**
     * How many records are in a Recordset object.
     * @return -1 when ADO cannot determine the number of records or if the provider or cursor type does not support RecordCount.
     * @throws COMInvokeException reading the RecordCount property on a closed Recordset
     */
    @ComProperty(name = "RecordCount")
    long getRecordCount();

    /**
     * <p>
     * memberId(1006)</p>
     */
    @ComProperty (name = "State")
    long state();

    @ComProperty(name = "EOF")
    Boolean isEOF();

    /**
     * <p>
     * memberId(1016)</p>
     */
    @ComMethod(name = "GetRows")
    OaIdl.SAFEARRAY getRows(int Rows,
                            Object Start,
                            Object Fields);

    /**
     * <p>
     * memberId(1016)</p>
     */
    @ComMethod(name = "GetRows")
    OaIdl.SAFEARRAY getRows(int Rows);

    /**
     * <p>
     * memberId(1016)</p>
     */
    @ComMethod(name = "GetRows")
    OaIdl.SAFEARRAY getRows();


    /**
     * <p>
     * memberId(1014)</p>
     */
    @ComMethod(name = "Close")
    void close();

    @ComMethod(name = "MoveFirst")
    void moveFirst();

    /**
     * Moves the current record position one record forward
     * @throws COMInvokeException An attempt to move forward when the isEOF is true
     */
    @ComMethod(name = "MoveNext")
    void moveNext();

    /**
     * <p>
     * memberId(1022)</p>
     */
    @ComMethod(name = "Open")
    void open(Object Source,
              Object ActiveConnection,
              CursorTypeEnum CursorType,
              LockTypeEnum LockType,
              int Options);
}


/**
 * <p>
 * guid({0000051B-0000-0010-8000-00AA006D2EA4})</p>
 */
enum CursorTypeEnum implements IComEnum {
    adOpenUnspecified(-1),
    adOpenForwardOnly(0),
    adOpenKeyset(1),
    adOpenDynamic(2),
    adOpenStatic(3),;

    CursorTypeEnum(long value) {
        this.value = value;
    }
    private final long value;

    public long getValue() {
        return this.value;
    }
}

/**
 * <p>
 * guid({0000051D-0000-0010-8000-00AA006D2EA4})</p>
 */
enum LockTypeEnum implements IComEnum {
    adLockUnspecified(-1),
    adLockReadOnly(1),
    adLockPessimistic(2),
    adLockOptimistic(3),
    adLockBatchOptimistic(4),;

    LockTypeEnum(long value) {
        this.value = value;
    }
    private final long value;

    public long getValue() {
        return this.value;
    }
}

