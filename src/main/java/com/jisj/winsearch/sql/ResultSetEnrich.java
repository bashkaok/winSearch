package com.jisj.winsearch.sql;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface extends {@link java.sql.ResultSet}
 */
public interface ResultSetEnrich {
    /**
     * Returns {@link Optional} value of column with index.
     *
     * @param columnIndex column index
     * @param <T>         type of value
     * @return {@link Optional} of column value. If there is an exception in ResultSet will be returned empty {@link Optional}
     */
    <T> Optional<T> getOptional(int columnIndex);

    /**
     * Returns true when the ResultSet has not records
     *
     * @throws SQLException called on closed ResultSet
     */
    boolean isEmpty() throws SQLException;

    /**
     * Returns number of records in ResultSet. ResultSet still is valid
     *
     * @return count of records
     * @throws SQLException called on closed ResultSet
     */
    long size() throws SQLException;

    /**
     * Stream of ResultSet
     *
     * @return {@link Stream Stream&lt;WinSearchResultSet&gt;} | empty {@link Stream} if an exception was thrown
     */
    Stream<WinSearchResultSet> stream();
}
