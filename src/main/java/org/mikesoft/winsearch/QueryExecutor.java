package org.mikesoft.winsearch;

import org.mikesoft.winsearch.sql.WinSearchConnection;
import org.mikesoft.winsearch.sql.WinSearchResultSet;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mikesoft.winsearch.QueryBuilder.FullTextPredicate.FreeText;

/**
 * Service for requests to MS Windows Index Search
 * <p>
 * Usage:<br>
 * &emsp;&emsp;&emsp;&emsp; executor = QueryExecutor.builder()<br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;.{@link QueryExecutorBuilder#properties(Property...) properties(Property...)} | {@link QueryExecutorBuilder#properties(String...) properties(String...)}<br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;.folders({@link java.nio.file.Path Path},{@link QueryBuilder.TraversalPredicate Traversal}...)<br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;[.{@link QueryExecutorBuilder#mapper(Function) mapper(Function&lt;ResultSet, Stream&lt;?>>)}<br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;.{@link QueryExecutorBuilder#connection(WinSearchConnection) connection(WinSearchConnection)}<br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;.build();<br>
 * &emsp;&emsp;&emsp;&emsp; ({@link Stream}) executor.{@link #find(String, boolean)}
 */
public class QueryExecutor {
    //TODO ResultSet Refactoring: make return Stream.Empty when exception
    public static final Function<WinSearchResultSet, Stream<?>> DEFAULT_MAPPER = resultSet -> {
        try {
            return resultSet.stream();
        } catch (SQLException e) {
            return Stream.empty();
        }
    };
    private WinSearchConnection connection;
    private String sqlStatement;
    private final List<String> propertyNames = new ArrayList<>();
    private final Set<QueryBuilder.Folder> folders = new HashSet<>();
    private QueryBuilder.FullTextPredicate fullTextPredicate = FreeText;
    private Set<String> fulltextColumns = new HashSet<>();
    private Function<WinSearchResultSet, Stream<?>> mapper;

    private QueryExecutor() {
        setMapper(DEFAULT_MAPPER);
    }

    /**
     * Adds string names of {@link Property} to SQL query. Duplicate property names are ignored
     *
     * @param properties {@link Property} names string values
     */
    public void addPropertyNames(List<String> properties) {
        properties.stream()
                .filter(p -> !propertyNames.contains(p))
                .forEach(propertyNames::add);
    }

    /**
     * Adds string names of {@link Property} to SQL query. Duplicate property names are ignored
     *
     * @param properties {@link Property}
     */
    public void addProperties(List<Property> properties) {
        addPropertyNames(properties.stream()
                .map(Property::getName)
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

    /**
     * Sets mapper for result stream.
     *
     * @param mapper {@link Function Function&lt;WinSearchResultSet, Stream&lt;?>>}
     */
    public void setMapper(Function<WinSearchResultSet, Stream<?>> mapper) {
        this.mapper = mapper;
    }

    public Function<WinSearchResultSet, Stream<?>> getMapper() {
        return mapper;
    }

    public void setConnection(WinSearchConnection connection) {
        this.connection = connection;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

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
    public Stream<?> find(String findStr, boolean strictMatch) {
        if (connection == null)
                throw new IllegalStateException("Connection not set");

        try (var st = connection.createStatement()) {
            WinSearchResultSet rs = (WinSearchResultSet) st.executeQuery(sqlStatement.formatted(buildFindStr(findStr, strictMatch)));
            System.out.println(rs.isClosed());
            return mapper.apply(rs);
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

        public QueryExecutorBuilder properties(Property... properties) {
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

        public QueryExecutorBuilder mapper(Function<WinSearchResultSet, Stream<?>> resultSetMapper) {
            executor.setMapper(resultSetMapper);
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
    }


}
