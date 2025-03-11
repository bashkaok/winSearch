package org.mikesoft.winsearch.sql;

import org.mikesoft.winsearch.ado.ADOConnection;

import java.sql.*;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import static org.mikesoft.winsearch.ado.ObjectStateEnum.adStateClosed;

/**
 * Wrapper for {@link ADOConnection}
 */
public class WinSearchConnection implements Connection {
    private final ADOConnection adoConnection;

    /**
     * Creates wrapper for {@link ADOConnection}
     * @param adoConnection {@link ADOConnection} object
     */
    public WinSearchConnection(ADOConnection adoConnection) {
        this.adoConnection = adoConnection;
    }

    public ADOConnection getAdoConnection() {
        return adoConnection;
    }

    /**
     * Creates empty statement {@link WinSearchStatement}
     * @return {@link WinSearchStatement}
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public Statement createStatement() throws SQLException {
        if (isClosed()) throw new WinSearchSQLException("Connection is closed");
        return new WinSearchStatement(this);
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

    /**
    * Unsupported
    */
    @Override
    public String nativeSQL(String sql) {
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

    /**
    * Unsupported
    */
    @Override
    public void rollback() {
    }

    /**
     * Interface implementation
     */
    @Override
    public void close() throws SQLException {
        adoConnection.close();
    }

    /**
     * Interface implementation
     */
    @Override
    public boolean isClosed() throws SQLException {//TODO Remove throwing
        return this.adoConnection.state() == adStateClosed.getValue();
    }

    @Override
    public DatabaseMetaData getMetaData() {
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
    public boolean isReadOnly() {
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

    /**
    * Unsupported
    */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) {
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
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() {
        return Map.of();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) {

    }

    @Override
    public void setHoldability(int holdability) {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) {
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
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
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
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) {
        return null;
    }

    @Override
    public Clob createClob() {
        return null;
    }

    @Override
    public Blob createBlob() {
        return null;
    }

    @Override
    public NClob createNClob() {
        return null;
    }

    @Override
    public SQLXML createSQLXML() {
        return null;
    }

    @Override
    public boolean isValid(int timeout) {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) {
    }

    @Override
    public void setClientInfo(Properties properties) {
    }

    @Override
    public String getClientInfo(String name) {
        return "";
    }

    @Override
    public Properties getClientInfo() {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) {
        return null;
    }

    /**
    * Unsupported
    */
    @Override
    public void setSchema(String schema) {
    }

    @Override
    public String getSchema() {
        return "";
    }

    @Override
    public void abort(Executor executor) {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) {
    }

    @Override
    public int getNetworkTimeout() {
        return 0;
    }

    /**
     * Returns proxy COM-object of {@link ADOConnection Connection(ADO)}
     * @param iface {@link ADOConnection Connection(ADO)}
     * @return proxy COM-object of {@link ADOConnection Connection(ADO)}
     * @param <T> {@link ADOConnection Connection(ADO)}
     * @throws SQLException If no object found that implements the interface
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) return (T) this.adoConnection;
        throw new WinSearchSQLException("No object found that implements the interface " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return Arrays.stream(adoConnection.getClass().getInterfaces())
                .anyMatch(i -> i == iface);
    }

}
