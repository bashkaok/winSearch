package org.mikesoft.winsearch.sql;

import org.mikesoft.winsearch.ado.COMFactory;

import java.sql.*;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import static org.mikesoft.winsearch.ado.ObjectStateEnum.adStateClosed;

/**
 * Wrapper for {@link org.mikesoft.winsearch.ado.Connection Connection(ADO)}
 */
public class ConnectionImpl implements Connection {
    private final org.mikesoft.winsearch.ado.Connection connection;

    /**
     * Creates connection with default connection string {@link COMFactory#CONNECTION_STR}
     */
    public ConnectionImpl() {
        this.connection = COMFactory.newNativeSystemIndexConnection();
    }

    /**
     * Creates empty statement {@link StatementImpl}
     * @return {@link StatementImpl}
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public Statement createStatement() throws SQLException {
        if (isClosed()) throw new SearchSQLException("Connection is closed");
        return new StatementImpl(connection);
    }

    /**
    * Unsupported
    */
    @Override
    public PreparedStatement prepareStatement(String sql) {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public CallableStatement prepareCall(String sql) {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return "";
    }

    /**
    * Unsupported
    */
    @Override
    public void setAutoCommit(boolean autoCommit) {

    }

    /**
    * Unsupported
    */
    @Override
    public boolean getAutoCommit() {
        return false;
    }

    /**
    * Unsupported
    */
    @Override
    public void commit()  {
    }

    @Override
    public void rollback() throws SQLException {

    }

    /**
     * Interface implementation
     */
    @Override
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * Interface implementation
     */
    @Override
    public boolean isClosed() throws SQLException {
        return this.connection.state() == adStateClosed.getValue();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public void setReadOnly(boolean readOnly) {

    }

    /**
     * Interface implementation. Windows Index is read-only system
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return true;
    }

    /**
    * Unsupported
    */
    @Override
    public void setCatalog(String catalog) {
    }

    /**
    * Unsupported
    */
    @Override
    public String getCatalog() {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public void setTransactionIsolation(int level) {

    }

    /**
     * Interface implementation
     */
    @Override
    public int getTransactionIsolation() {
        return TRANSACTION_NONE;
    }

    /**
    * Unsupported
    */
    @Override
    public SQLWarning getWarnings() {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public void clearWarnings()  {
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return Map.of();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public void rollback(Savepoint savepoint) {
    }

    /**
    * Unsupported
    */
    @Override
    public void releaseSavepoint(Savepoint savepoint) {
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return "";
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        return "";
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    /**
     * Returns proxy COM-object of {@link org.mikesoft.winsearch.ado.Connection Connection(ADO)}
     * @param iface {@link org.mikesoft.winsearch.ado.Connection Connection(ADO)}
     * @return proxy COM-object of {@link org.mikesoft.winsearch.ado.Connection Connection(ADO)}
     * @param <T> {@link org.mikesoft.winsearch.ado.Connection Connection(ADO)}
     * @throws SQLException If no object found that implements the interface
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) return (T) this.connection;
        throw new SearchSQLException("No object found that implements the interface " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return Arrays.stream(connection.getClass().getInterfaces())
                .anyMatch(i -> i == iface);
    }

}
