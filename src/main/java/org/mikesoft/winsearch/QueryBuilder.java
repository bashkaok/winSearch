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
     * @param folders           where will be done the searching List of {@link Folder Folder}
     * @param fullTextPredicate full-text or one column searching {@link FullText FullText}
     * @param fullText          Optional. If the array is specified, the searching will be complete only in specified columns
     * @return SQL formatted String: "SELECT ... FROM SystemIndex WHERE SCOPE|DIRECTORY=(...) AND FREETEXT|CONTAINS(..., %s)"
     */
    public static String build(List<String> properties,
                               List<Folder> folders,
                               FullText fullTextPredicate,
                               String... fullText) {

        if (properties.isEmpty())
            throw new IllegalArgumentException("The property list cannot be empty");

        if (fullText.length > 0 && !new HashSet<>(properties).containsAll(List.of(fullText)))
            throw new IllegalArgumentException("Full-text columns must be in property list");

        List<String> statement = new ArrayList<>();
        statement.add(buildSelectClause(properties));
        statement.add("FROM SystemIndex");
        statement.add(buildWhereClause(folders, fullTextPredicate,
                Arrays.stream(fullText).toList()));

        return String.join("\n", statement);
    }

    /**
     * Overloaded method {@link #build(List, List, FullText, String...)}
     *
     * @param properties        List of {@link Property}
     * @param folders           where will be done the searching List of {@link Folder Folder}
     * @param fullTextPredicate full-text or one column searching {@link FullText FullText}
     * @param fullText          List of {@link Property}
     * @return SQL formatted string
     */
    public static String build(List<Property> properties,
                               List<Folder> folders,
                               FullText fullTextPredicate,
                               Property... fullText) {

        return build(properties.stream().map(Property::getName).toList(),
                folders, fullTextPredicate,
                Arrays.stream(fullText)
                        .map(Property::getName)
                        .toArray(String[]::new)
        );


    }

    private static String buildSelectClause(List<String> properties) {
        return "SELECT " + String.join(DELIMITER, properties);
    }

    private static String buildWhereClause(List<Folder> folders, FullText fullTextPredicate, List<String> fullText) {
        final String WHERE_DELIMITER = " AND ";
        List<String> clause = new ArrayList<>();
        buildFolderPart(folders)
                .filter(str -> !str.isEmpty())
                .map(str -> "(" + str + ")")
                .ifPresent(clause::add);
        clause.add(buildFullTextPart(fullTextPredicate, fullText));
        return "WHERE " + String.join(WHERE_DELIMITER, clause);
    }

    /**
     * Build part of WHERE clause ... WHERE [{<a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth">SCOPE | DIRECTORY</a>}='<protocol>:[{SID}]<path>']
     *
     * @param folders list of search paths
     * @return string with part of WHERE clause: SCOPE|DIRECTORY='&lt;protocol&gt;:&lt;path&gt;'
     */
    private static Optional<String> buildFolderPart(List<Folder> folders) {
        final String FOLDER_DELIMITER = " OR ";
        return Optional.of(folders.stream()
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
     * @param fullTextPredicate {@link FullText FullText} predicate: {@link FullText#Contains Contain} | {@link FullText#FreeText FreeText}
     * @param fullTextColumns   column names
     * @return string with part of WHERE clause
     */
    private static String buildFullTextPart(FullText fullTextPredicate, List<String> fullTextColumns) {
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
    public enum Traversal {
        Shallow("DIRECTORY"),
        Deep("SCOPE");
        private final String predicate;

        Traversal(String predicate) {
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
    public enum FullText {
        Contains("CONTAINS"),
        FreeText("FREETEXT");
        private final String predicate;

        FullText(String predicate) {
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
     * Container for pair {@link Path}:{@link Traversal Traversal}
     */
    public static class Folder {
        private final Path path;
        private final QueryBuilder.Traversal traversal;

        private Folder(Path path, QueryBuilder.Traversal traversal) {
            this.path = path;
            this.traversal = traversal;
        }

        public Path getPath() {
            return path;
        }

        public QueryBuilder.Traversal getTraversal() {
            return traversal;
        }

        public static Folder of(Path path, QueryBuilder.Traversal traversal) {
            return new Folder(path, traversal);
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