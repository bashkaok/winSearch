package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * Specifies the current position of the record pointer within a Recordset
 * <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/positionenum?view=sql-server-ver15">MS Learn</a>
 * @see ADORecordset#absolutePosition()
 */
public enum PositionEnum implements IComEnum {
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
