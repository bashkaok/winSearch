package com.jisj.winsearch.ado;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * Specifies the type of lock placed on records during editing
 * @see <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/locktypeenum?view=sql-server-ver15">MS learn</a>
 */
//guid({0000051D-0000-0010-8000-00AA006D2EA4} ?
public enum LockTypeEnum implements IComEnum {
    /**
     * Does not specify a type of lock. For clones, the clone is created with the same lock type as the original
     */
    adLockUnspecified(-1),
    /**
     * Indicates read-only records. You cannot alter the data
     */
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
