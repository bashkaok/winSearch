package org.mikesoft.winsearch.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

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
     * @return {@link Stream} with {@link ResultSet}
     * @throws SQLException when called on closed {@link ResultSet}
     */
    Stream<ResultSet> stream() throws SQLException;
}
