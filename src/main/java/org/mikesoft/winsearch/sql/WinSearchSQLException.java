package org.mikesoft.winsearch.sql;

import java.sql.SQLException;

public class WinSearchSQLException extends SQLException {
    public WinSearchSQLException(Throwable cause) {
        super(cause);
    }

    public WinSearchSQLException(String reason) {
        super(reason);
    }
}
