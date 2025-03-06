package org.mikesoft.winsearch;

import java.sql.SQLException;

public class SearchSQLException extends SQLException {
    public SearchSQLException(Throwable cause) {
        super(cause);
    }

    public SearchSQLException(String reason) {
        super(reason);
    }
}
