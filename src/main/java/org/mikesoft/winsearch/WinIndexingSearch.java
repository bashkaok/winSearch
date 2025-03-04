package org.mikesoft.winsearch;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.OaIdlUtil;
import com.sun.jna.platform.win32.Ole32;
import org.mikesoft.winsearch.win.ADOConnection;
//import org.mikesoft.winsearch.win.CursorTypeEnum;
//import org.mikesoft.winsearch.win.LockTypeEnum;
import org.mikesoft.winsearch.win.ADORecordset;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WinIndexingSearch implements SearchEngine {
    private static final Logger     LOGGER = Logger.getLogger(WinIndexingSearch.class.getName());
    public static final String      ADODB_CONNECTION = "Provider=Search.CollatorDSO;Extended Properties='Application=Windows';";
    public static final String[][]  NULL_RESULT = new String[0][0];
    private final CharSequence      fieldsDelimiter;
    private final Property[]        properties;
    private int                     searchColumn = -1;
    private int                     resultColumn = 0;
    private final ADOConnection connection;
    private final ArrayList<SearchFolder> searchFolders;
    private ADORecordset recordset;

    /**
     * @param properties    properties for searching {@link Property}
     */
    public WinIndexingSearch(Property[] properties) {
        this(properties, ";");
    }

    /**
     *
     * @param properties properties for searching {@link Property}
     * @param fieldsDelimiter delimiter char for result list, default = ";"
     */
    public WinIndexingSearch(Property[] properties, CharSequence fieldsDelimiter) {
        this.fieldsDelimiter = fieldsDelimiter;
        this.properties = properties;
        this.searchFolders = new ArrayList<>();
        connection = getConnection(ADODB_CONNECTION);
        if (connection != null) {
            ObjectFactory factory = new ObjectFactory();
            recordset = factory.createObject(ADORecordset.class);
        }
    }

    public void addSearchFolder(Path folder, boolean scopeDepth) {
        searchFolders.add(new SearchFolder(folder, scopeDepth));
    }

    /**
     * Column for full-text searching
     * @param searchColumn -1 all indexed text properties are searched
     */
    public void setSearchColumn(int searchColumn) {
        if (searchColumn >= properties.length )
            throw new RuntimeException("Index is bigger properties count");
        this.searchColumn = searchColumn;
    }

    public void setResultColumn(int resultColumn) {
        if (resultColumn >= properties.length )
            throw new RuntimeException("Index is bigger properties count");
        this.resultColumn = resultColumn;
    }

    public static ADOConnection getConnection(String connectionString) {
        Ole32.INSTANCE.CoInitializeEx(null,Ole32.COINIT_APARTMENTTHREADED);
        ObjectFactory factory = new ObjectFactory();
        ADOConnection connection;
        try {
            connection = factory.createObject(ADOConnection.class);
        } catch (COMException ex) {
            LOGGER.log(Level.WARNING, "Connection error", ex);
            return null;
        }
        try {
            connection.Open(connectionString, "", "", -1);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Connection error", ex);
            return null;
        }
        return connection;

    }

    public static String replaceIllegalChars(String source) {
        return source.replaceAll("[\\n.!,)(]", "_");
    }

    private static String prepareFindStr(String findStr) {
        return replaceIllegalChars(findStr.replaceAll("'", "''"));
    }

    /** <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-windowssearch-entry">
     * Prepare query for MS Indexing search </a>
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-3x-wds-propertymappings"> <br>
     * Property mapping</a>
     * @param findStr string with search pattern
     * @return SQL sentence
     */
    private static String prepareQuery(String findStr, boolean strictMatch,
                                       Property[] properties, int searchColumn,
                                       ArrayList<SearchFolder> searchFolders) {

        // https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-select
        String searchFields = Arrays.stream(properties).map(property -> property.value)
                                                       .collect(Collectors.joining(", "));
        String selectStatement = "SELECT " + searchFields + " FROM SystemIndex ";

        // https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth}
        String depthPredicates = searchFolders.isEmpty()? "" : searchFolders.stream()
                .map(folder -> (folder.scopeDepth? "SCOPE='file:" : "DIRECTORY='file:") + folder.path.toString()+"'")
                .collect(Collectors.joining(" OR "));

        // https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-fulltextpredicates
        findStr = prepareFindStr(findStr);
        if (strictMatch) findStr = findStr.replaceAll(" ", "*");
        String fullTextPredicates = (strictMatch? "CONTAINS" : "FREETEXT") + "(" +
                                    (searchColumn == -1? "*" : properties[searchColumn].value ) + ", '" +
                                     findStr + "')";

        String whereStatement = "WHERE " + (!depthPredicates.isBlank()? "(" + depthPredicates + ") AND " : "")
                                         + fullTextPredicates;

        String orderByClause = " ORDER BY " + searchFields;

        String buildSQL = selectStatement + whereStatement +  orderByClause;
        LOGGER.log(Level.FINE, "sqlDuery=" + buildSQL);
        return buildSQL;
    }

    public static Object[][] doQuery(ADOConnection connection, ADORecordset recordset, String sqlStr) {
        if (recordset.state() == 1) recordset.close();
        try {
//            recordset.Open(sqlStr, connection, CursorTypeEnum.adOpenUnspecified, LockTypeEnum.adLockUnspecified, -1);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Query error " + sqlStr, ex);
        }
        if (recordset.state() == 0 || recordset.isEOF()) return NULL_RESULT;
        return (Object[][]) OaIdlUtil.toPrimitiveArray(recordset.getRows(), true);
    }

    public static ArrayList<String> toList(String[][] data, CharSequence fieldsDelimiter) {
        ArrayList<String> result = new ArrayList<>();
        for (Object[] datum : data) {
            String record = Arrays.stream(datum).map(item->(String)item).collect(Collectors.joining(fieldsDelimiter));
            result.add(record);
        }
        return result;
    }

    @Override
    public List<Path> getFiles(String findStr, boolean strictMatch) {
        String sql = prepareQuery(findStr, strictMatch, properties, searchColumn, searchFolders);
        return Arrays.stream(doQuery(connection, recordset, sql ))
                                  .map(item->(String)item[resultColumn])
                                  .map(Path::of).toList();
    }

    /**
     * For keeping list of folders for searching. Uses at SCOPE & DIRECTORY predicates <br>
     * this.scope = true - SCOPE predicate, false - DIRECTORY predicate <br>
     * <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-sql-folderdepth">SCOPE and DIRECTORY predicates</a>
     */
    public static class SearchFolder {
        private final Path path;
        private final boolean scopeDepth;
        public SearchFolder(Path path, boolean scopeDepth) {
            this.path = path;
            this.scopeDepth = scopeDepth;
        }
    }
}
