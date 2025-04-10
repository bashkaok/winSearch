package com.jisj.winsearch;

import com.jisj.winsearch.properties.WinProperty;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Static methods for SQL query building according to <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-windowssearch-entry">Windows Search SQL Syntax</a>
 */
public class QueryBuilder {
    private static final String DELIMITER = ", ";

    private QueryBuilder() {
    }

    /**
     * Build the SQL query according to <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-windowssearch-entry">Windows Search SQL Syntax</a>
     * <p>
     * Supports next statements and clauses:<br>
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-select">SELECT</a> &lt;columns&gt; <br>
     * FROM SystemIndex
     * WHERE [{<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth">SCOPE | DIRECTORY</a>}='&lt;protocol&gt;:[{<a href="https://learn.microsoft.com/en-us/windows/win32/secauthz/security-identifiers">SID</a>}]&lt;path&gt;'] <br>
     * [AND]<br> [ <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-contains">CONTAINS</a>(["&lt;fulltext_column>",]'&lt;contains_condition&gt;'[,&lt;<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-specifyinglanguages">LCID</a>>])<br>
     * | <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-freetext">FREETEXT</a>(["&lt;fulltext_column>",]'&lt;freetext_condition>'[,&lt;LCID&gt;])]<br>
     * | &lt;column> &lt;{@link ComparisonPredicate comparison_predicate}> &lt;conditions>
     * <p>
     * Example for Comparison.Contains | Comparison.FreeText:<pre>
     *     {@code sql = QueryBuilder.build(
     *          List.of(Property.SystemItemPathDisplay, Property.SystemFileName),
     *          List.of(Path.of("D:\\Tools\\test-test_path")),
     *          Depth.Deep,
     *          Comparison.Contains // | Comparison.FreeText
     *          );
     *
     * sql ==   "SELECT System.ItemPathDisplay, System.FileName
     *          FROM SystemIndex
     *          WHERE (SCOPE='file:D:\\Tools\\test-test_path') AND CONTAINS(*, '%s')"}
     * </pre>
     * Example for another predicates:<pre>
     *      {@code sql = QueryBuilder.build(
     *          List.of(Property.SystemItemPathDisplay, Property.SystemFileName),
     *          List.of(Path.of("D:\\Tools\\test-test_path")),
     *          Depth.Deep,
     *          Comparison.EqualTo,
     *          Property.SystemItemPathDisplay, Property.SystemFileName
     *          );
     *
     *  sql ==  "SELECT System.ItemPathDisplay, System.FileName
     *          FROM SystemIndex
     *          WHERE (SCOPE='file:D:\\Tools\\test-test_path') AND SystemItemPathDisplay = '%s'
     *                 AND SystemFileName = '%s'"}
     *
     * </pre>
     * For further use: {@code sql.formatted("matching conditions" [,"matching conditions"]);}
     *
     * @param properties        for select clause column
     * @param folders           where will be done the searching, Set of {@link Folder Folder}
     * @param comparisonPredicate {@link ComparisonPredicate ComparisonPredicate}
     * @param columns           the limit of the search to a single column or a column group
     * @return SQL formatted String: "SELECT ... WHERE ... '%s'"
     */
    public static String build(List<String> properties,
                               Set<Folder> folders,
                               ComparisonPredicate comparisonPredicate,
                               Set<String> columns) {

        if (properties.isEmpty())
            throw new IllegalArgumentException("The property list cannot be empty");

        if (!columns.isEmpty() && !new HashSet<>(properties).containsAll(columns))
            throw new IllegalArgumentException("Full-text columns must be in property list");

        List<String> statement = new ArrayList<>();
        statement.add(buildSelectClause(properties));
        statement.add("FROM SystemIndex");
        statement.add(buildWhereClause(folders, comparisonPredicate, columns));

        return String.join("\n", statement);
    }

    /**
     * Overloaded method {@link #build(List, Set, ComparisonPredicate, Set)}
     *
     * @param properties        list of property string names
     * @param folders           where will be done the searching, Set of {@link Folder Folder}
     * @param comparisonPredicate {@link ComparisonPredicate ComparisonPredicate}
     * @param columns   Optional. The limit of the search to a single column or a column group {@link String} column names. Duplicate columns are ignored
     * @return SQL formatted string: "SELECT ... WHERE ...'%s'"
     */
    public static String build(List<String> properties,
                               Set<Folder> folders,
                               ComparisonPredicate comparisonPredicate,
                               String... columns) {
        return build(properties, folders, comparisonPredicate, Arrays.stream(columns).collect(Collectors.toSet()));
    }


    /**
     * Overloaded method {@link #build(List, Set, ComparisonPredicate, Set)}
     *
     * @param properties        List of {@link WinProperty WinProperty}
     * @param folders           where will be done the searching, Set of {@link Folder Folder}
     * @param comparisonPredicate {@link ComparisonPredicate ComparisonPredicate}
     * @param columns           Optional. The limit of the search to a single column or a column group {@link WinProperty WinProperty} columns. Duplicate columns are ignored
     * @return @return SQL formatted string: "SELECT ... WHERE ...'%s'"
     */
    public static String build(List<WinProperty> properties,
                               Set<Folder> folders,
                               ComparisonPredicate comparisonPredicate,
                               WinProperty... columns) {

        return build(properties.stream().map(WinProperty::getName).toList(),
                folders,
                comparisonPredicate,
                Arrays.stream(columns)
                        .map(WinProperty::getName)
                        .collect(Collectors.toSet())
        );
    }

    private static String buildSelectClause(List<String> properties) {
        return "SELECT " + String.join(DELIMITER, properties);
    }

    private static String buildWhereClause(Set<Folder> folders, ComparisonPredicate comparisonPredicate, Set<String> columns) {
        final String WHERE_DELIMITER = " AND ";
        List<String> clause = new ArrayList<>();
        buildFolderPart(folders)
                .filter(str -> !str.isEmpty())
                .map(str -> "(" + str + ")")
                .ifPresent(clause::add);
        switch (comparisonPredicate) {
            case Contains, FreeText ->
                    clause.add(buildFullTextPart(comparisonPredicate, columns.stream().sorted().toList()));
            case EqualTo -> clause.addAll(buildComparisonPart(comparisonPredicate, columns.stream().sorted().toList()));
        }
        return "WHERE " + String.join(WHERE_DELIMITER, clause);
    }

    /**
     * Build part of WHERE clause ... WHERE [{<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth">SCOPE | DIRECTORY</a>}='<protocol>:[{SID}]<path>']
     *
     * @param folders list of search paths
     * @return string with part of WHERE clause: SCOPE|DIRECTORY='&lt;protocol&gt;:&lt;path&gt;'
     */
    private static Optional<String> buildFolderPart(Set<Folder> folders) {
        final String FOLDER_DELIMITER = " OR ";
        return Optional.of(folders.stream()
                .sorted(Comparator.comparing(Folder::getPath))
                .map(folder -> folder.getDepth().getPredicate() + "='" +
                        folder.getPath().toUri().getScheme() + ":" +
                        folder.getPath().toAbsolutePath() + "'")
                .collect(Collectors.joining(FOLDER_DELIMITER)));
    }

    /**
     * Build <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-fulltextpredicates">Full-Text predicates</a> part of WHERE clause:<br>
     * {@code
     * WHERE... CONTAINS(["<fulltext_column>",]'<contains_condition>'[,<LCID>])
     * WHERE... FREETEXT(["<fulltext_column>",]'<freetext_condition>'[,<LCID>])]}
     *
     * @param comparisonPredicate {@link ComparisonPredicate ComparisonPredicate} predicate: {@link ComparisonPredicate#Contains Contain} | {@link ComparisonPredicate#FreeText FreeText}
     * @param columns             comparison column names
     * @return string with part of WHERE clause
     */
    private static String buildFullTextPart(ComparisonPredicate comparisonPredicate, List<String> columns) {
        return comparisonPredicate.getPredicate() + "(" +
                (columns.isEmpty() ? "*" : String.join(",", columns)) +
                ", '%s'" +
                ")";
    }

    private static List<String> buildComparisonPart(ComparisonPredicate comparisonPredicate, List<String> columns) {
        if (columns.isEmpty())
            throw new IllegalArgumentException("Columns for comparison with <" + comparisonPredicate.getPredicate() + "> not founds");
        return columns.stream()
                .map(column -> column + comparisonPredicate.getPredicate() + "'%s'")
                .toList();
    }

    /**
     * Folder depth predicates control the scope of a search by specifying a path and whether to perform a deep or shallow traversal
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth">MS Learn</a>
     */
    public enum DepthPredicate {
        /**
         * Predicate performs a shallow traversal of only the folder specified
         */
        Shallow("DIRECTORY"),
        /**
         * Predicate performs a deep traversal of the path, including all subfolders
         */
        Deep("SCOPE");
        private final String predicate;

        DepthPredicate(String predicate) {
            this.predicate = predicate;
        }

        public String getPredicate() {
            return predicate;
        }
    }

    /**
     * The Microsoft Windows Search query full-text search predicates
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-fulltextpredicates">MS Learn</a> and comparison predicates
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-nonfulltextpredicates">MS Learn</a>
     */
    public enum ComparisonPredicate {
        /**
         * The fulltext predicate performs comparisons on columns that contain text
         * <p>The CONTAINS clause can perform matching on single words or phrases, based on the proximity of the search terms, for ex.:
         * <ul>
         * <li>{@code WHERE ... CONTAINS(SystemItemName, 'folder name'}</li>
         * <li>{@code WHERE ... CONTAINS(*, 'folder name'}</li>
         * </ul>
         */
        Contains("CONTAINS"),
        /**
         * The fulltext predicate is tuned to match the meaning of the search phrases against text columns
         */
        FreeText("FREETEXT"),
        /**
         * Equal to column value
         * ex: {@code WHERE ... SystemItemName = 'folder name'}
         */
        EqualTo("=");
        private final String predicate;

        ComparisonPredicate(String predicate) {
            this.predicate = predicate;
        }

        /**
         * Returns string value of predicate {@link #Contains "CONTAINS"} or {@link #FreeText "FREETEXT"}
         *
         * @return string {@link #Contains CONTAINS} | {@link #FreeText FREETEXT}
         */
        public String getPredicate() {
            return predicate;
        }
    }

    /**
     * Container for pair {@link Path}:{@link DepthPredicate Traversal}
     */
    public static class Folder {
        private final Path path;
        private final DepthPredicate depth;

        private Folder(Path path, DepthPredicate depth) {
            this.path = path;
            this.depth = depth;
        }

        /**
         * Getter
         *
         * @return folder {@link Path}
         */
        public Path getPath() {
            return path;
        }

        public DepthPredicate getDepth() {
            return depth;
        }

        /**
         * Creates {@link Folder}
         *
         * @param path  {@link Path} of folder
         * @param depth of traversal {@link DepthPredicate#Shallow Shallow} | {@link DepthPredicate#Deep Deep}
         * @return new {@link Folder} object
         */
        public static Folder of(Path path, DepthPredicate depth) {
            return new Folder(path, depth);
        }

        /**
         * Creates {@link Folder}
         *
         * @param path  {@link String} of folder
         * @param depth of traversal {@link DepthPredicate#Shallow Shallow} | {@link DepthPredicate#Deep Deep}
         * @return new {@link Folder} object
         */

        public static Folder of(String path, DepthPredicate depth) {
            return new Folder(Path.of(path), depth);
        }


        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            Folder folder = (Folder) object;
            return getPath().equals(folder.getPath());
        }

        @Override
        public int hashCode() {
            return getPath().hashCode();
        }

        @Override
        public String toString() {
            return "Folder{" +
                    "path=" + path +
                    ", traversal=" + depth +
                    '}';
        }
    }

}