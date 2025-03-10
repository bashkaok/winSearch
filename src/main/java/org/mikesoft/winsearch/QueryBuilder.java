package org.mikesoft.winsearch;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Static methods for SQL query building according to <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-windowssearch-entry">Windows Search SQL Syntax</a>
 */
public class QueryBuilder {
    private static final String DELIMITER = ", ";

    /**
     * Build the SQL query according to <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-windowssearch-entry">Windows Search SQL Syntax</a>
     * <p>
     * Supports next statements and clauses:<br>
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-select">SELECT</a> &lt;columns&gt; <br>
     * FROM SystemIndex
     * WHERE [{<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth">SCOPE | DIRECTORY</a>}='&lt;protocol&gt;:[{<a href="https://learn.microsoft.com/en-us/windows/win32/secauthz/security-identifiers">SID</a>}]&lt;path&gt;'] <br>
     * [AND] [<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-contains">CONTAINS</a>(["&lt;fulltext_column>",]'&lt;contains_condition&gt;'[,&lt;<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-specifyinglanguages">LCID</a>>]) |<br>
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-freetext">FREETEXT</a>(["&lt;fulltext_column>",]'&lt;freetext_condition>'[,&lt;LCID&gt;])]
     * <p>
     * Example: invoke of QueryBuilder.build(<br>
     * &emsp;&emsp;&emsp;&emsp; List.of(Property.SystemItemPathDisplay, Property.SystemFileName),<br>
     * &emsp;&emsp;&emsp;&emsp; List.of(Path.of("D:\\Tools\\test-test_path")),<br>
     * &emsp;&emsp;&emsp;&emsp; QueryBuilder.Traversal.Deep,<br>
     * &emsp;&emsp;&emsp;&emsp; QueryBuilder.FullText.Contains);<br>
     * returns string sql = "SELECT System.ItemPathDisplay, System.FileName<br>
     * &emsp;&emsp;&emsp;&emsp; FROM SystemIndex <br>
     * &emsp;&emsp;&emsp;&emsp; WHERE (SCOPE='file:D:\\Tools\\test-test_path') AND CONTAINS(*, '%s')"<br>
     * For further use: sql.formatted("matching conditions");<br>
     *
     * @param properties        for select clause column
     * @param folders           where will be done the searching, Set of {@link Folder Folder}
     * @param fullTextPredicate {@link FullTextPredicate#FreeText FREETEXT} or {@link FullTextPredicate#Contains CONTAINS} searching {@link FullTextPredicate FullTextPredicate}
     * @param fullTextColumns   the limit of the search to a single column or a column group
     * @return SQL formatted String: "SELECT ... FROM SystemIndex WHERE SCOPE|DIRECTORY=(...) AND FREETEXT|CONTAINS(..., %s)"
     */
    public static String build(List<String> properties,
                               Set<Folder> folders,
                               FullTextPredicate fullTextPredicate,
                               Set<String> fullTextColumns) {

        if (properties.isEmpty())
            throw new IllegalArgumentException("The property list cannot be empty");

        if (!fullTextColumns.isEmpty() && !new HashSet<>(properties).containsAll(fullTextColumns))
            throw new IllegalArgumentException("Full-text columns must be in property list");

        List<String> statement = new ArrayList<>();
        statement.add(buildSelectClause(properties));
        statement.add("FROM SystemIndex");
        statement.add(buildWhereClause(folders, fullTextPredicate, fullTextColumns));

        return String.join("\n", statement);
    }

    /**
     * Overloaded method {@link #build(List, Set, FullTextPredicate, Set)}
     *
     * @param properties        list of property string names
     * @param folders           where will be done the searching, Set of {@link Folder Folder}
     * @param fullTextPredicate {@link FullTextPredicate#FreeText FREETEXT} or {@link FullTextPredicate#Contains CONTAINS} searching {@link FullTextPredicate FullTextPredicate}
     * @param fullTextColumns   Optional. List of {@link String} column names. Duplicate columns are ignored
     * @return SQL formatted string: "SELECT ... WHERE ...'%s'"
     */
    public static String build(List<String> properties,
                               Set<Folder> folders,
                               FullTextPredicate fullTextPredicate,
                               String... fullTextColumns) {
        return build(properties, folders, fullTextPredicate, Arrays.stream(fullTextColumns).collect(Collectors.toSet()));
    }


    /**
     * Overloaded method {@link #build(List, Set, FullTextPredicate, Set)}
     *
     * @param properties        List of {@link Property}
     * @param folders           where will be done the searching, Set of {@link Folder Folder}
     * @param fullTextPredicate {@link FullTextPredicate#FreeText FREETEXT} or {@link FullTextPredicate#Contains CONTAINS} searching {@link FullTextPredicate FullTextPredicate}
     * @param fullTextColumns   Optional. List of {@link Property} columns. Duplicate columns are ignored
     * @return @return SQL formatted string: "SELECT ... WHERE ...'%s'"
     */
    public static String build(List<Property> properties,
                               Set<Folder> folders,
                               FullTextPredicate fullTextPredicate,
                               Property... fullTextColumns) {

        return build(properties.stream().map(Property::getName).toList(),
                folders,
                fullTextPredicate,
                Arrays.stream(fullTextColumns)
                        .map(Property::getName)
                        .collect(Collectors.toSet())
        );
    }

    private static String buildSelectClause(List<String> properties) {
        return "SELECT " + String.join(DELIMITER, properties);
    }

    private static String buildWhereClause(Set<Folder> folders, FullTextPredicate fullTextPredicate, Set<String> fullTextColumns) {
        final String WHERE_DELIMITER = " AND ";
        List<String> clause = new ArrayList<>();
        buildFolderPart(folders)
                .filter(str -> !str.isEmpty())
                .map(str -> "(" + str + ")")
                .ifPresent(clause::add);
        clause.add(buildFullTextPart(fullTextPredicate, fullTextColumns.stream().sorted().toList()));
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
                .map(folder -> folder.getTraversal().getPredicate() + "='" +
                        folder.getPath().toUri().getScheme() + ":" +
                        folder.getPath().toAbsolutePath() + "'")
                .collect(Collectors.joining(FOLDER_DELIMITER)));
    }

    /**
     * Build <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-fulltextpredicates">Full-Text predicates</a> part of WHERE clause:<br>
     * WHERE... [<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-contains">CONTAINS</a>(["&lt;fulltext_column>",]'&lt;contains_condition>'[,&lt;<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-specifyinglanguages">LCID</a>>])<br>
     * | <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-freetext">FREETEXT</a>(["&lt;fulltext_column>",]'&lt;freetext_condition>'[,&lt;LCID>])]
     *
     * @param fullTextPredicate {@link FullTextPredicate FullText} predicate: {@link FullTextPredicate#Contains Contain} | {@link FullTextPredicate#FreeText FreeText}
     * @param fullTextColumns   column names
     * @return string with part of WHERE clause
     */
    private static String buildFullTextPart(FullTextPredicate fullTextPredicate, List<String> fullTextColumns) {
        return fullTextPredicate.getPredicate() + "(" +
                (fullTextColumns.isEmpty() ? "*" : String.join(",", fullTextColumns)) +
                ", '%s'" +
                ")";
    }

    /**
     * Folder depth predicates control the scope of a search by specifying a path and whether to perform a deep or shallow traversal
     *
     * @see <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth">SCOPE and DIRECTORY Predicates</a>
     */
    public enum TraversalPredicate {
        Shallow("DIRECTORY"),
        Deep("SCOPE");
        private final String predicate;

        TraversalPredicate(String predicate) {
            this.predicate = predicate;
        }

        public String getPredicate() {
            return predicate;
        }
    }

    /**
     * The Microsoft Windows Search query full-text search predicates
     * <p>
     * The CONTAINS predicate performs comparisons on columns that contain text. The CONTAINS clause can perform matching on single words or phrases, based on the proximity of the search terms. In comparison, the FREETEXT predicate is tuned to match the meaning of the search phrases against text columns
     *
     * @see <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-fulltextpredicates">Full-Text Predicates</a>
     */
    public enum FullTextPredicate {
        Contains("CONTAINS"),
        FreeText("FREETEXT");
        private final String predicate;

        FullTextPredicate(String predicate) {
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
     * Container for pair {@link Path}:{@link TraversalPredicate Traversal}
     */
    public static class Folder {
        private final Path path;
        private final TraversalPredicate traversal;

        private Folder(Path path, TraversalPredicate traversal) {
            this.path = path;
            this.traversal = traversal;
        }

        public Path getPath() {
            return path;
        }

        public TraversalPredicate getTraversal() {
            return traversal;
        }

        public static Folder of(Path path, TraversalPredicate traversal) {
            return new Folder(path, traversal);
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
                    ", traversal=" + traversal +
                    '}';
        }
    }

}