package org.mikesoft.winsearch.sql;

import com.sun.jna.platform.win32.COM.COMInvokeException;
import org.mikesoft.winsearch.ado.COMFactory;
import org.mikesoft.winsearch.ado.ADORecordset;
import org.mikesoft.winsearch.ado.CursorTypeEnum;
import org.mikesoft.winsearch.ado.LockTypeEnum;

import java.sql.*;

/**
 * Implementation of {@link Statement}
 */
public class WinSearchStatement implements Statement {
    private final WinSearchConnection connection;
    private WinSearchResultSet resultSet;
    private boolean closed = false;
    private String query;

    public WinSearchStatement(WinSearchConnection connection) {
        this.connection = connection;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        ADORecordset rs = COMFactory.newADORecordSet();
        this.query = sql;
        try {
            rs.open(sql, connection.getAdoConnection(),
                    CursorTypeEnum.adOpenStatic,
                    LockTypeEnum.adLockReadOnly,
                    ADORecordset.adCmdUnspecified);
        } catch (COMInvokeException e) {
            if (e.getHresult().intValue() == 0x80020009)
                throw new WinSearchSQLException("Error of retrieving data from SQL: possibly faulty SQL query\n" + sql);
            throw new RuntimeException(e);
        }
        if (!rs.isBOF()) rs.movePrevious();
        resultSet = new WinSearchResultSet(rs, this);
        return resultSet;
    }

    /**
     * Unsupported
     */
    @Override
    public int executeUpdate(String sql) {
        return 0;
    }

    /**
     * Closes the {@link WinSearchStatement}. Also closes the {@link WinSearchResultSet}, if it exists and wrapped {@link ADORecordset}
     *
     * @throws SQLException if a access error occurs in {@link ADORecordset} wrapped in {@link WinSearchResultSet}
     */
    @Override
    public void close() throws SQLException {
        this.closed = true;
        if (resultSet != null) resultSet.close();
    }

    @Override
    public int getMaxFieldSize() {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) {

    }

    @Override
    public int getMaxRows() {
        return 0;
    }

    @Override
    public void setMaxRows(int max) {

    }

    @Override
    public void setEscapeProcessing(boolean enable) {
    }

    @Override
    public int getQueryTimeout() {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) {
    }

    /**
     * Unsupported
     */
    @Override
    public boolean execute(String sql) {
        return false;
    }

    /**
     * Retrieves the current result as a ResultSet object. This method can be called more once per result
     *
     * @return {@link WinSearchResultSet} | null if the result is an update count or there are no more results
     * @throws WinSearchSQLException when called on a closed Statement
     */
    @Override
    public ResultSet getResultSet() throws SQLException {
        assertClosedStatement();
        return resultSet;
    }

    @Override
    public int getUpdateCount() {
        return 0;
    }

    @Override
    public boolean getMoreResults() {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) {
    }

    @Override
    public int getFetchDirection() {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String sql) {

    }

    @Override
    public void clearBatch() {

    }

    @Override
    public int[] executeBatch() {
        return new int[0];
    }

    /**
     * Retrieves the Connection object that produced this Statement object.
     *
     * @return current Connection object
     * @throws WinSearchSQLException when called on a closed Statement
     */
    @Override
    public Connection getConnection() throws SQLException {
        assertClosedStatement();
        return connection;
    }

    /**
    * Unsupported
    */
    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public ResultSet getGeneratedKeys() {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) {
        return false;
    }

    @Override
    public int getResultSetHoldability() {
        return 0;
    }

    /**
     * Retrieves whether this Statement object has been closed. A Statement is closed if the method close has been called on it, or if it is automatically closed
     *
     * @return true if this Statement object is closed; false if it is still open
     * @see #close()
     */
    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setPoolable(boolean poolable) {

    }

    @Override
    public boolean isPoolable() {
        return false;
    }

    /**
     * Unsupported
     */
    @Override
    public void closeOnCompletion() {

    }

    @Override
    public boolean isCloseOnCompletion() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    private void assertClosedStatement() throws SQLException {
        if (isClosed()) throw new WinSearchSQLException("Statement is closed");
    }

    @Override
    public String toString() {
        return "WinSearchStatement{" +
                "connection=" + connection +
                ", resultSet=" + resultSet +
                ", closed=" + closed +
                ", query='\n" + query + '\'' +
                '}';
    }
}
