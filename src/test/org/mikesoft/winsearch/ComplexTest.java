package org.mikesoft.winsearch;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ComplexTest {

    @Test
    void statementQuery() {
        final String sql = """
                    SELECT System.ItemName, System.FileName, System.ItemNameDisplay
                    FROM SystemIndex
                    WHERE SCOPE='file:D:/Tools/Java/winSearch/src/test/resources/test-data'
                    """;

        DataSource ds = new DataSourceImpl();
        try (var con = ds.getConnection();
             var st = con.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            assertEquals(1, ((ResultSetImpl)rs).size());
            int count = 0;
            while (rs.next()) {
                rs.getObject(0);
                count++;
            }
            assertEquals(1,count);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
