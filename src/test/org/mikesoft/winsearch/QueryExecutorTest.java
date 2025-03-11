package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;
import org.mikesoft.winsearch.properties.Core;
import org.mikesoft.winsearch.sql.WinSearchConnection;
import org.mikesoft.winsearch.sql.WinSearchDataSource;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mikesoft.winsearch.QueryBuilder.FullTextPredicate.Contains;
import static org.mikesoft.winsearch.QueryBuilder.TraversalPredicate.Deep;
import static org.mikesoft.winsearch.QueryBuilder.TraversalPredicate.Shallow;
import static org.mikesoft.winsearch.QueryBuilder.Folder;

class QueryExecutorTest {

    @Test
    void builder() {
        QueryExecutor executor = QueryExecutor.builder()
                .properties(Core.SystemFileName, Core.SystemFileExtension, Core.SystemItemPathDisplay)
                .properties("System.FileName", "System.FileExtension")
                .folders(Folder.of(Path.of("Folder1"), Shallow),
                        Folder.of(Path.of("Folder2"), Deep),
                        Folder.of(Path.of("Folder1"), Deep)
                )
                .fullTextPredicate(Contains)
                .fullTextColumns("System.FileName")
                .connection(null)
                .build();

        assertNotNull(executor);
        assertEquals(3, executor.getProperties().size());
        assertEquals(List.of("System.FileName", "System.FileExtension", "System.ItemPathDisplay"), executor.getProperties());
        assertEquals(Contains, executor.getFullTextPredicate());
        assertEquals(2, executor.getFolders().size());
        assertEquals(1, executor.getFulltextColumns().size());
        System.out.println(executor.getSqlStatement());
    }

    @Test
    void find() throws SQLException {
        final WinSearchDataSource dataSource = new WinSearchDataSource();
        final Path dataPath = Path.of("src/test/resources/test-data");

        WinSearchConnection con = (WinSearchConnection) dataSource.getConnection();
        //default mapper
        QueryExecutor executor = QueryExecutor.builder()
                .properties(Core.SystemFileName, Core.SystemItemPathDisplay)
                .folders(Folder.of(dataPath,Shallow))
                .fullTextPredicate(Contains)
                .connection(con)
                .build();
        assertEquals(0, executor.find("bla-bla", true).count());
        assertEquals(1, executor.find("standby", false).count());

        //custom mapper
        QueryExecutor.Mapper<List<Path>> mapper = resultSet -> resultSet
                .map(rs -> rs.getOptional(1))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(p -> Path.of((String) p))
                .toList();

        executor = QueryExecutor.builder()
                .properties(Core.SystemFileName, Core.SystemItemPathDisplay)
                .folders(Folder.of(dataPath, Shallow))
                .fullTextPredicate(Contains)
                .connection(con)
                .build();

        assertEquals(0, executor.find("bla-bla", true, mapper).size());
        assertEquals(1, executor.find("standby", false, mapper).size());
        assertInstanceOf(List.class, executor.find("standby", false, mapper));
        assertInstanceOf(Path.class, executor.find("standby", false, mapper).getFirst());

        //manually build
        executor = QueryExecutor.builder().buildEmpty();
        String SQL = """
                SELECT System.FileName, System.ItemPathDisplay
                FROM SystemIndex
                WHERE (DIRECTORY='file:D:\\Tools\\Java\\winSearch\\src\\test\\resources\\test-data') AND CONTAINS(*, '%s')
                """;
        executor.setSqlStatement(SQL);
        executor.setConnection(con);
        assertEquals(1, executor.find("standby", false, mapper).size());
    }

}