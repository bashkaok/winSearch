package org.mikesoft.winsearch.ado;


import com.sun.jna.platform.win32.COM.util.IComEnum;
import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.OaIdl;

import java.util.Arrays;

/**
 * Mapped interface of ADODB.Recordset.6.0 (clsId = {00000535-0000-0010-8000-00AA006D2EA4})
 *
 * @see <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/recordset-object-ado?view=sql-server-ver15">Recordset Object (ADO)</a>
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
     * @return long record position or {@link PositionEnum PositionEnum} constant
     */
    @ComProperty(name = "AbsolutePosition")
    long absolutePosition();

    /**
     * Property to control how many records to retrieve at one time into local memory from the provider. Default is 1
     * @return size of cache
     */
    @ComProperty(name = "CacheSize")
    long cacheSize();

    /**
     * Sets {@link #cacheSize()}
     * @param size Long value that must be greater than 0. Default is 1
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
     * <p>
     * memberId(1006)</p>
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
    OaIdl.SAFEARRAY getRows(int Rows,
                            Object Start,
                            Object Fields);

    /**
     * After you call GetRows, the next unread record becomes the current record, or the EOF property is set to True if there are no more records.
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

    @ComMethod(name = "MovePrevious")
    void movePrevious();

    /**
     * <p>
     * memberId(1022)</p>
     */
    @ComMethod(name = "Open")
    void open(Object source,
              Object activeConnection,
              CursorTypeEnum CursorType,
              LockTypeEnum LockType,
              int Options);

    @ComMethod(name = "Open")
    void open(Object source,
              Object activeConnection);

    // guid={0000051B-0000-0010-8000-00AA006D2EA4}?
    enum CursorTypeEnum implements IComEnum {
        adOpenUnspecified(-1),
        adOpenForwardOnly(0),
        adOpenKeySet(1),
        adOpenDynamic(2),
        adOpenStatic(3),
        ;

        CursorTypeEnum(long value) {
            this.value = value;
        }

        private final long value;

        @Override
        public long getValue() {
            return this.value;
        }

        /**
         * Returns the enum constant of this class with the specified
         *
         * @param value of cursor
         * @return {@link CursorTypeEnum CursorTypeEnum}
         */
        public static CursorTypeEnum valueOf(long value) {
            return Arrays.stream(values())
                    .filter(i -> i.getValue() == value)
                    .findAny()
                    .orElse(null);
        }
    }

    //guid({0000051D-0000-0010-8000-00AA006D2EA4} ?
    enum LockTypeEnum implements IComEnum {
        adLockUnspecified(-1),
        adLockReadOnly(1),
        adLockPessimistic(2),
        adLockOptimistic(3),
        adLockBatchOptimistic(4),
        ;

        LockTypeEnum(long value) {
            this.value = value;
        }

        private final long value;

        public long getValue() {
            return this.value;
        }
    }

    /**
     * <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/positionenum?view=sql-server-ver15">PositionEnum</a>
     */
    enum PositionEnum implements IComEnum {
        /**
         * Indicates that the current record pointer is at BOF (that is, the BOF property is True)
         */
        adPosBOF(-2),
        /**
         * Indicates that the current record pointer is at EOF (that is, the EOF property is True)
         */
        adPosEOF(-3),
        /**
         * Indicates that the Recordset is empty, the current position is unknown, or the provider does not support the AbsolutePage or AbsolutePosition property
         */
        adPosUnknown(-1);

        PositionEnum(long value) {
            this.value = value;
        }

        private final long value;

        public long getValue() {
            return this.value;
        }
    }


}

