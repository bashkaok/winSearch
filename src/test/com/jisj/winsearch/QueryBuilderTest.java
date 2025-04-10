package com.jisj.winsearch;

import org.junit.jupiter.api.Test;
import com.jisj.winsearch.properties.Core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {
    private static final Path test_path = Path.of("D:\\Tools\\test-test_path");

    @Test
    void build_assertions() {
        //Empty property list
        assertThrowsExactly(IllegalArgumentException.class, () -> QueryBuilder.build(
                new ArrayList<String>(),
                Set.of(QueryBuilder.Folder.of(test_path, QueryBuilder.DepthPredicate.Shallow)),
                QueryBuilder.ComparisonPredicate.FreeText)
        );

        //Full-text columns not in property list
        assertThrowsExactly(IllegalArgumentException.class, () -> QueryBuilder.build(
                        List.of("System.ItemPathDisplay", "System.FileName"),
                        Set.of(QueryBuilder.Folder.of(test_path, QueryBuilder.DepthPredicate.Shallow)),
                        QueryBuilder.ComparisonPredicate.FreeText,
                        "System.FileDisplayName"
                )
        );
    }

    @Test
    void build() {
        final String shallow_freeText_Sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE (DIRECTORY='file:D:\\Tools\\test-test_path') AND FREETEXT(*, '%s')
                """;
        assertEquals(shallow_freeText_Sql.trim(), QueryBuilder.build(
                List.of(Core.SystemItemPathDisplay, Core.SystemFileName),
                Set.of(QueryBuilder.Folder.of(test_path, QueryBuilder.DepthPredicate.Shallow)),
                QueryBuilder.ComparisonPredicate.FreeText
        ).trim());

        final String deep_contains_Sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE (SCOPE='file:D:\\Tools\\test-test_path') AND CONTAINS(*, '%s')
                """;
        assertEquals(deep_contains_Sql.trim(), QueryBuilder.build(
                List.of(Core.SystemItemPathDisplay, Core.SystemFileName),
                Set.of(QueryBuilder.Folder.of(test_path, QueryBuilder.DepthPredicate.Deep)),
                QueryBuilder.ComparisonPredicate.Contains
        ).trim());

        final String comparison_Sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE (DIRECTORY='file:D:\\Tools\\test-test_path') AND System.FileName='%s'
                """;
        assertEquals(comparison_Sql.trim(), QueryBuilder.build(
                List.of(Core.SystemItemPathDisplay, Core.SystemFileName),
                Set.of(QueryBuilder.Folder.of(test_path, QueryBuilder.DepthPredicate.Shallow)),
                QueryBuilder.ComparisonPredicate.EqualTo,
                Core.SystemFileName
        ).trim());
    }

    @Test
    void build_multiple_folders() {
        final String sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE (DIRECTORY='file:D:\\Tools\\test-path2' OR SCOPE='file:D:\\Tools\\test-test_path') AND CONTAINS(System.FileName,System.ItemPathDisplay, '%s')
                """;
        assertEquals(sql.trim(), QueryBuilder.build(
                List.of(Core.SystemItemPathDisplay, Core.SystemFileName),
                Set.of(QueryBuilder.Folder.of(test_path, QueryBuilder.DepthPredicate.Deep),
                        QueryBuilder.Folder.of(Path.of("D:\\Tools\\test-path2"), QueryBuilder.DepthPredicate.Shallow)),
                QueryBuilder.ComparisonPredicate.Contains,
                Core.SystemItemPathDisplay, Core.SystemFileName
        ).trim());
    }


    @Test
    void build_empty_folder_list() {
        final String sql = """
                SELECT System.ItemPathDisplay, System.FileName
                FROM SystemIndex
                WHERE CONTAINS(System.FileName,System.ItemPathDisplay, '%s')
                """;
        assertEquals(sql.trim(), QueryBuilder.build(
                List.of(Core.SystemItemPathDisplay, Core.SystemFileName),
                Set.of(),
                QueryBuilder.ComparisonPredicate.Contains,
                Core.SystemItemPathDisplay, Core.SystemFileName
        ).trim());

    }
}