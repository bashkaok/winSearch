package org.mikesoft.winsearch.ado;

import com.sun.jna.platform.win32.COM.util.IComEnum;

import java.util.Arrays;

/**
 * Specifies the type of cursor used in a {@link ADORecordset Recordset} object.
 * @see <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/cursortypeenum?view=sql-server-ver15">MS Learn</a>
 */
// guid={0000051B-0000-0010-8000-00AA006D2EA4}?
public enum CursorTypeEnum implements IComEnum {
    /**
     * Does not specify the type of cursor
     */
    adOpenUnspecified(-1),
    /**
     * Default. Uses a forward-only cursor. Identical to a static cursor, except that you can only scroll forward through records. This improves performance when you need to make only one pass through a Recordset
     */
    adOpenForwardOnly(0),
    /**
     * Uses a keyset cursor. Like a dynamic cursor, except that you can't see records that other users add, although records that other users delete are inaccessible from your Recordset. Data changes by other users are still visible
     */
    adOpenKeySet(1),
    /**
     * Uses a dynamic cursor. Additions, changes, and deletions by other users are visible, and all types of movement through the Recordset are allowed, except for bookmarks, if the provider doesn't support them
     */
    adOpenDynamic(2),
    /**
     * Uses a static cursor, which is a static copy of a set of records that you can use to find data or generate reports. Additions, changes, or deletions by other users are not visible
     */
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
