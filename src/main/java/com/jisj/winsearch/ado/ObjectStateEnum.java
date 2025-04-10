package com.jisj.winsearch.ado;

import java.util.Arrays;

/**
 * Specifies whether an object is open or closed, connecting to a data source, executing a command, or retrieving data
 *  <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/objectstateenum?view=sql-server-ver15">MS Learn</a>
 * @see ADOConnection
 * @see ADORecordset
 */
public enum ObjectStateEnum {
    /**
     * Indicates that the object is closed
     */
    adStateClosed(0),
    /**
     * Indicates that the object is open
     */
    adStateOpen(1),
    /**
     * Indicates that the object is connecting
     */
    adStateConnecting(2),
    /**
     * Indicates that the object is executing a command
     */
    adStateExecuting(4),
    /**
     * Indicates that the rows of the object are being retrieved
     */
    adStateFetching(8);

    private final long state;

    ObjectStateEnum(long state) {
        this.state = state;
    }

    public long getValue() {
        return state;
    }

    /**
     * Returns the enum constant of this class with the specified value
     *
     * @param value object state code
     * @return constant of this class
     */
    public static ObjectStateEnum valueOf(long value) {
        return Arrays.stream(values()).filter(item -> item.getValue() == value).findAny().orElse(null);
    }
}
