package org.mikesoft.winsearch;

import org.mikesoft.winsearch.properties.WinProperty;
import org.mikesoft.winsearch.sql.WinSearchConnection;
import org.mikesoft.winsearch.sql.WinSearchResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mikesoft.winsearch.QueryBuilder.ComparisonPredicate.Contains;
import static org.mikesoft.winsearch.QueryBuilder.ComparisonPredicate.FreeText;

/**
 * Service for requests to MS Windows Index Search<br>
 * <p>Usage:
 * <pre>{@code      executor = QueryExecutor.builder()
 *              .properties(WinProperty...) | properties(String...)
 *              .folders(Folder.of(Path, TraversalPredicate)...) // optional
 *              .fullTextPredicate(FreeText | Contains]
 *              .fullTextColumns(WinProperty...) | fullTextColumns(String...) // optional
 *              .connection(WinSearchConnection)
 *              .build();
 *      executor.find(String, boolean [, mapper]);
 *      }
 * </pre>
 * You can use manually build:
 * <pre> {@code
 *         executor = QueryExecutor.builder().buildEmpty();
 *         String SQL = """
 *                 SELECT System.FileName, System.ItemPathDisplay
 *                 FROM SystemIndex
 *                 WHERE (DIRECTORY='file:D:/test-data') AND CONTAINS(*, '%s')
 *                 """;
 *         executor.setSqlStatement(SQL);
 *         executor.setConnection(con);
 *         executor.find(smth, true [, mapper]);}
 * </pre>
 */
public class QueryExecutor {
    private static final Mapper<Stream<WinSearchResultSet>> DEFAULT_MAPPER = stream -> stream;
    private WinSearchConnection connection;
    private String sqlStatement;
    private final List<String> propertyNames = new ArrayList<>();
    private final Set<QueryBuilder.Folder> folders = new HashSet<>();
    private QueryBuilder.ComparisonPredicate fullTextPredicate = FreeText;
    private final Set<String> fulltextColumns = new HashSet<>();
    private String resultStatement;

    private QueryExecutor() {
    }

    /**
     * Adds string names of {@link org.mikesoft.winsearch.properties.WinProperty WinProperty} to SQL query. Duplicate property names are ignored
     *
     * @param properties {@link org.mikesoft.winsearch.properties.WinProperty WinProperty} names string values
     */
    public void addPropertyNames(List<String> properties) {
        properties.stream()
                .filter(p -> !propertyNames.contains(p))
                .forEach(propertyNames::add);
    }

    /**
     * Adds string names of {@link org.mikesoft.winsearch.properties.WinProperty WinProperty} to SQL query. Duplicate property names are ignored
     *
     * @param properties {@link org.mikesoft.winsearch.properties.WinProperty WinProperty}
     */
    public void addProperties(List<WinProperty> properties) {
        addPropertyNames(properties.stream()
                .map(WinProperty::getName)
                .toList());
    }

    public List<String> getProperties() {
        return propertyNames;
    }

    public void addFolders(Set<QueryBuilder.Folder> folders) {
        this.folders.addAll(folders);
    }

    public Set<QueryBuilder.Folder> getFolders() {
        return folders;
    }

    /**
     * Sets {@link QueryBuilder.ComparisonPredicate FullText} predicate
     *
     * @param fullTextPredicate default {@link QueryBuilder.ComparisonPredicate#FreeText FreeText}
     */
    public void setFullTextPredicate(QueryBuilder.ComparisonPredicate fullTextPredicate) {
        this.fullTextPredicate = fullTextPredicate;
    }

    public QueryBuilder.ComparisonPredicate getFullTextPredicate() {
        return fullTextPredicate;
    }

    /**
     * Adds fulltext column to SQL query
     *
     * @param columns names of columns. Duplicate column names are ignored
     */
    public void addFullTextColumns(Set<String> columns) {
        fulltextColumns.addAll(columns);
    }

    public Set<String> getFulltextColumns() {
        return fulltextColumns;
    }

    public void setConnection(WinSearchConnection connection) {
        this.connection = connection;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    /**
     * Sets ready SQL query to Windows Index Search
     * <p>Further call of the {@link #buildQuery()} replaces this statement
     *
     * @param sqlStatement SQL string
     */
    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    /**
     * Gets state of {@link org.mikesoft.winsearch.sql.WinSearchStatement WinSearchStatement} after query execution
     *
     * @return string with state of statement, query and number of retrieved records
     */
    @SuppressWarnings("unused")
    public String getResultStatement() {
        return resultStatement;
    }

    /**
     * Builds query on this class field values
     */
    public void buildQuery() {
        sqlStatement = QueryBuilder.build(
                propertyNames,
                folders,
                fullTextPredicate,
                fulltextColumns);
    }

    /**
     * Finds the specified String with match condition. Overloaded method {@link #find(String, Mapper) find()}
     *
     * @param findStr     String to find
     * @throws IllegalStateException when the service is closed
     */

    public Stream<WinSearchResultSet> find(String findStr) {
        return find(findStr, DEFAULT_MAPPER);
    }

    /**
     * Interface for simplify use of the mapper function
     * {@link Function}{@code <Stream<WinSearchResultSet>, R>}
     *
     * @param <R> type of mapper result
     */
    @FunctionalInterface
    public interface Mapper<R> extends Function<Stream<WinSearchResultSet>, R> {
    }

    /**
     * Finds the specified String with match condition
     *
     * @param findStr     String to find
     * @param mapper      {@link Mapper Mapper&lt;R>}.
     * @param <R>         type of mapper result
     * @return mapper result
     * @throws IllegalStateException when the service is closed
     */
    public <R> R find(String findStr, Mapper<R> mapper) {
        if (connection == null)
            throw new IllegalStateException("Connection not set");
        assertFindString(findStr);

        try {
            Statement st = connection.createStatement();
            WinSearchResultSet rs = (WinSearchResultSet) st.executeQuery(sqlStatement.formatted(findStr));
            resultStatement = st + "\nRecords retrieved: " + rs.size();
            return mapper.apply(rs.stream(st));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertFindString(String findStr) {
        if (fullTextPredicate == Contains && findStr.contains(" ") && !FindStringBuilder.isQuotationEnclosed(findStr))
            throw new IllegalArgumentException("Find string contains the <space> symbols. For use with CONTAINS predicate the string should be enclosed in quotation marks");
    }

    /**
     * Calls builder for the class
     *
     * @return {@link QueryExecutorBuilder} object
     */
    public static QueryExecutorBuilder builder() {
        return new QueryExecutorBuilder(new QueryExecutor());
    }


    /**
     * Builder for {@link QueryExecutor}
     */
    public static class QueryExecutorBuilder {
        private final QueryExecutor executor;

        private QueryExecutorBuilder(QueryExecutor executor) {
            this.executor = executor;
        }

        public QueryExecutorBuilder properties(WinProperty... properties) {
            executor.addProperties(Arrays.stream(properties).toList());
            return this;
        }

        /**
         * Adds select columns to object
         * @param properties String name of columns
         * @return {@link QueryExecutorBuilder} object
         */
        public QueryExecutorBuilder properties(String... properties) {
            executor.addPropertyNames(Arrays.stream(properties).toList());
            return this;
        }

        public QueryExecutorBuilder folders(QueryBuilder.Folder... folders) {
            executor.addFolders(Arrays.stream(folders).collect(Collectors.toSet()));
            return this;
        }

        public QueryExecutorBuilder comparisonPredicate(QueryBuilder.ComparisonPredicate fullTextPredicate) {
            executor.setFullTextPredicate(fullTextPredicate);
            return this;
        }

        /**
         * Adds constraint columns to object
         * @param columns String names columns from properties set {@link #properties(String...)}
         * @return {@link QueryExecutorBuilder} object
         */
        public QueryExecutorBuilder constraintColumns(String... columns) {
            executor.addFullTextColumns(Arrays.stream(columns).collect(Collectors.toSet()));
            return this;
        }

        /**
         * Adds constraint columns to object
         * @param columns {@link WinProperty} columns from properties set {@link #properties(WinProperty...)}
         * @return {@link QueryExecutorBuilder} object
         */
        @SuppressWarnings("unused")
        public QueryExecutorBuilder constraintColumns(WinProperty... columns) {
            executor.addFullTextColumns(Arrays.stream(columns).map(WinProperty::getName).collect(Collectors.toSet()));
            return this;
        }


        public QueryExecutorBuilder connection(WinSearchConnection connection) {
            executor.setConnection(connection);
            return this;
        }

        /**
         * Final method for build. Calls {@link QueryExecutor#buildQuery()}
         * @return ready {@link QueryExecutor} object
         */
        public QueryExecutor build() {
            executor.buildQuery();
            return executor;
        }

        /**
         * Builds empty QueryExecutor object. All settings should be done manually
         *
         * @return {@link QueryExecutor} object
         */
        public QueryExecutor buildEmpty() {
            return new QueryExecutor();
        }

    }


}
