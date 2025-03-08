package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {

    @Test
    void build() {
        final Path test_path = Path.of("D:\\Tools\\test-test_path");

        final String shallow_freeText_Sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE (DIRECTORY='file:D:\\Tools\\test-test_path') AND FREETEXT(*, '%s')
                """;
        assertEquals(shallow_freeText_Sql.trim(), QueryBuilder.build(
                List.of(Property.SystemItemPathDisplay, Property.SystemFileName),
                List.of(test_path),
                QueryBuilder.Traversal.Shallow,
                QueryBuilder.FullText.FreeText
        ).trim());

        final String deep_contains_Sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE (SCOPE='file:D:\\Tools\\test-test_path') AND CONTAINS(*, '%s')
                """;
        assertEquals(deep_contains_Sql.trim(), QueryBuilder.build(
                List.of(Property.SystemItemPathDisplay, Property.SystemFileName),
                List.of(test_path),
                QueryBuilder.Traversal.Deep,
                QueryBuilder.FullText.Contains
        ).trim());

        final String deep_contains_multiple_Sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE (SCOPE='file:D:\\Tools\\test-test_path' OR SCOPE='file:D:\\Tools\\test-path2') AND CONTAINS(System.ItemPathDisplay,System.FileName, '%s')
                """;
        assertEquals(deep_contains_multiple_Sql.trim(), QueryBuilder.build(
                List.of(Property.SystemItemPathDisplay, Property.SystemFileName),
                List.of(test_path, Path.of("D:\\Tools\\test-path2")),
                QueryBuilder.Traversal.Deep,
                QueryBuilder.FullText.Contains,
                Property.SystemItemPathDisplay, Property.SystemFileName
        ).trim());


    }
}