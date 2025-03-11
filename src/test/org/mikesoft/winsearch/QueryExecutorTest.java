package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.sql.WinSearchConnection;
import org.mikesoft.winsearch.sql.WinSearchDataSource;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.QueryBuilder.FullTextPredicate.Contains;
import static org.mikesoft.winsearch.QueryBuilder.TraversalPredicate.Deep;
import static org.mikesoft.winsearch.QueryBuilder.TraversalPredicate.Shallow;
import static org.mikesoft.winsearch.QueryBuilder.Folder;

class QueryExecutorTest {

    @Test
    void builder() throws SQLException {
        QueryExecutor executor = QueryExecutor.builder()
                .properties(Property.SystemFileName, Property.SystemFileExtension, Property.SystemItemPathDisplay)
                .properties("System.FileName", "System.FileExtension")
                .folders(Folder.of(Path.of("Folder1"),
                                Shallow),
                        Folder.of(Path.of("Folder2"), Deep),
                        Folder.of(Path.of("Folder1"), Deep)
                )
                .fullTextPredicate(Contains)
                .fullTextColumns("System.FileName")
                .mapper(null)
                .connection(null)
                .build();

        assertNotNull(executor);
        assertEquals(3, executor.getProperties().size());
        assertEquals(List.of("System.FileName", "System.FileExtension", "System.ItemPathDisplay"), executor.getProperties());
        assertEquals(Contains, executor.getFullTextPredicate());
        assertEquals(2, executor.getFolders().size());
        assertEquals(1, executor.getFulltextColumns().size());
        assertNull(executor.getMapper());
        System.out.println(executor.getSqlStatement());
    }

    @Test
    void find() throws SQLException {
        final WinSearchDataSource dataSource = new WinSearchDataSource();

        WinSearchConnection con = (WinSearchConnection) dataSource.getConnection();
        QueryExecutor executor = QueryExecutor.builder()
                .properties(Property.SystemFileName, Property.SystemItemPathDisplay)
                .folders(Folder.of(Path.of("src/test/resources/test-data"), Shallow))
                .fullTextPredicate(Contains)
                .mapper(resultSet -> {
                    List<String> result= new ArrayList<>();
                    try {
                        while (resultSet.next()) {
                            result.add((String) resultSet.getOptional(0).orElse(""));
                        }
                        return result.stream();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .connection(con)
                .build();
        assertEquals(0, executor.find("bla-bla", true).count());
        assertEquals(0, executor.find("standby", false).count());
        assertFalse(con.isClosed());

    }
}