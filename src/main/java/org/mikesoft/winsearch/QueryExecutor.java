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

import static org.mikesoft.winsearch.QueryBuilder.FullTextPredicate.FreeText;

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
    private QueryBuilder.FullTextPredicate fullTextPredicate = FreeText;
    private final Set<String> fulltextColumns = new HashSet<>();

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
     * Sets {@link QueryBuilder.FullTextPredicate FullText} predicate
     *
     * @param fullTextPredicate default {@link QueryBuilder.FullTextPredicate#FreeText FreeText}
     */
    public void setFullTextPredicate(QueryBuilder.FullTextPredicate fullTextPredicate) {
        this.fullTextPredicate = fullTextPredicate;
    }

    public QueryBuilder.FullTextPredicate getFullTextPredicate() {
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
     * Finds the specified String with match condition
     *
     * @param findStr     String to find
     * @param strictMatch true - strict match
     * @throws IllegalStateException when the service is closed
     */

    public Stream<WinSearchResultSet> find(String findStr, boolean strictMatch) {
        return find(findStr, strictMatch, DEFAULT_MAPPER);
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
     * @param strictMatch true - strict match
     * @param mapper      {@link Mapper Mapper&lt;R>}.
     * @param <R>         type of mapper result
     * @return mapper result
     * @throws IllegalStateException when the service is closed
     */
    public <R> R find(String findStr, boolean strictMatch, Mapper<R> mapper) {
        if (connection == null)
            throw new IllegalStateException("Connection not set");

        try {
            Statement st = connection.createStatement();
            WinSearchResultSet rs = (WinSearchResultSet) st.executeQuery(sqlStatement.formatted(buildFindStr(findStr, strictMatch)));
            return mapper.apply(rs.stream(st));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildFindStr(String findStr, boolean strictMatch) {
        if (strictMatch) return prepareFindStr(findStr).replaceAll(" ", "*");
        else return prepareFindStr(findStr);
    }

    private static String prepareFindStr(String findStr) {
        return findStr.replaceAll("'", "''")
                .replaceAll("[\\n.!,)(]", "_");
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

        public QueryExecutorBuilder(QueryExecutor executor) {
            this.executor = executor;
        }

        public QueryExecutorBuilder properties(WinProperty... properties) {
            executor.addProperties(Arrays.stream(properties).toList());
            return this;
        }

        public QueryExecutorBuilder properties(String... properties) {
            executor.addPropertyNames(Arrays.stream(properties).toList());
            return this;
        }

        public QueryExecutorBuilder folders(QueryBuilder.Folder... folders) {
            executor.addFolders(Arrays.stream(folders).collect(Collectors.toSet()));
            return this;
        }

        public QueryExecutorBuilder fullTextPredicate(QueryBuilder.FullTextPredicate fullTextPredicate) {
            executor.setFullTextPredicate(fullTextPredicate);
            return this;
        }

        public QueryExecutorBuilder fullTextColumns(String... columns) {
            executor.addFullTextColumns(Arrays.stream(columns).collect(Collectors.toSet()));
            return this;
        }


        public QueryExecutorBuilder connection(WinSearchConnection connection) {
            executor.setConnection(connection);
            return this;
        }

        public QueryExecutor build() {
            executor.buildQuery();
            return executor;
        }

        /**
         * Builds empty QueryExecutor object. All settings should be done manually
         * @return {@link QueryExecutor} object
         */
        public QueryExecutor buildEmpty() {
            return new QueryExecutor();
        }

    }


}
