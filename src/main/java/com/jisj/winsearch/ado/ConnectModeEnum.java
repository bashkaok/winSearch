package com.jisj.winsearch.ado;

/**
 * Specifies the available permissions for modifying data in a {@link ADOConnection Connection}, opening a <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/record-object-ado?view=sql-server-ver15">Record</a>
 * <a href="https://learn.microsoft.com/en-us/previous-versions/sql/ado/reference/ado-api/connectmodeenum?view=sql-server-ver15">MS Learn</a>
 *
 * @see ADOConnection Connection
 */
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
