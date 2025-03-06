package org.mikesoft.winsearch.ado;

import java.util.Arrays;

/**
 * @see <a href=https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/objectstateenum?view=sql-server-ver15>ObjectStateEnum</a>
 */
public enum ObjectState {
    adStateClosed(0),       //Indicates that the object is closed.
    adStateOpen(1), 	    //Indicates that the object is open.
    adStateConnecting(2),   //Indicates that the object is connecting.
    adStateExecuting(4),    //Indicates that the object is executing a command.
    adStateFetching(8)      //Indicates that the rows of the object are being retrieved.
    ;
    private final long state;

    ObjectState(long state) {
        this.state = state;
    }

    public long getValue() {
        return state;
    }

    public static ObjectState valueOf(long value) {
        return Arrays.stream(values())
                .filter(item-> item.getValue() == value)
                .findAny()
                .orElse(null);
    }
}
