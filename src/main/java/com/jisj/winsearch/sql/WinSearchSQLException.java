package com.jisj.winsearch.sql;

import java.sql.SQLException;

/**
 * Subclass for {@link SQLException}
 */
public class WinSearchSQLException extends SQLException {
    public WinSearchSQLException(Throwable cause) {
        super(cause);
    }

    public WinSearchSQLException(String reason) {
        super(reason);
    }
}
