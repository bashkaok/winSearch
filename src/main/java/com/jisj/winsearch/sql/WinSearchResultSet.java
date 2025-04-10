package com.jisj.winsearch.sql;

import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.OaIdl;
import com.jisj.winsearch.ado.ObjectStateEnum;
import com.jisj.winsearch.ado.ADORecordset;
import com.jisj.winsearch.utils.OaIdlUtil;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Wrapper for {@link ADORecordset}
 */
public class WinSearchResultSet implements ResultSet, ResultSetEnrich {
    private final ADORecordset recordset;
    private final Statement statement;
    private final CurrentRow currentRow = new CurrentRow();

    public WinSearchResultSet(ADORecordset adoRecordset) {
        this(adoRecordset, null);
    }

    public WinSearchResultSet(ADORecordset recordset, Statement statement) {
        this.recordset = recordset;
        this.statement = statement;
    }

    /**
     * Create {@link Stream} of {@link WinSearchResultSet}
     *
     * @param resultSet       {@link WinSearchResultSet}
     * @param objectsForClose Optional. Objects with implemented interface {@link AutoCloseable} that should be closed after stream processing
     * @return {@link Stream}&lt;{@link WinSearchResultSet}>
     */
    public static Stream<WinSearchResultSet> streamOf(WinSearchResultSet resultSet, AutoCloseable... objectsForClose) {
        long size;
        try {
            if (resultSet.isClosed()) return Stream.empty();
            resultSet.beforeFirst();
            size = resultSet.size();
        } catch (SQLException e) {
            return Stream.empty();
        }
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<>(size, Spliterator.IMMUTABLE) {
            @Override
            public boolean tryAdvance(Consumer<? super WinSearchResultSet> action) {
                try {
                    if (!resultSet.next()) {
                        if (objectsForClose.length > 0) {
                            for (AutoCloseable closeable : objectsForClose) closeable.close();
                        } else resultSet.beforeFirst();
                        return false;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                action.accept(resultSet);
                return true;
            }
        }, false);
    }


    @Override
    public long size() throws SQLException {
        assertClosedResultSet();
        return comInvokeExWrap(recordset::getRecordCount);
    }

    @Override
    public boolean isEmpty() throws SQLException {
        assertClosedResultSet();
        return comInvokeExWrap(recordset::isBOF) && comInvokeExWrap(recordset::isEOF);
    }

    private void setCurrentRecord() throws SQLException {
        assertReadingOutOfBounds();
        OaIdl.SAFEARRAY ar = comInvokeExWrap(() -> recordset.getRows(1));
        Object[][] record = (Object[][]) OaIdlUtil.toPrimitiveArray(ar, true);
        currentRow.setRow(record[0]);
    }

    private void assertReadingOutOfBounds() throws SQLException {
        if (isBeforeFirst()) throw new WinSearchSQLException("Try reading before first record");
        if (isAfterLast()) throw new WinSearchSQLException("Try reading after last record");
    }

    private void assertClosedResultSet() throws SQLException {
        if (isClosed()) throw new WinSearchSQLException("ResultSet is closed");
    }

    /**
     * Moves the cursor forward one row from its current position
     */
    @Override
    public boolean next() throws SQLException {
        assertClosedResultSet();
        if (isEmpty() || isAfterLast()) return false;
        if (isBeforeFirst()) comInvokeExWrap(recordset::moveNext);
        setCurrentRecord();
        return true;
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) return;
        comInvokeExWrap(recordset::close);
    }

    /**
     * Unsupported
     */
    @Override
    public boolean wasNull() {
        return false;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return getObject(columnIndex, String.class);
    }

    @Override
    public boolean getBoolean(int columnIndex) {
        return false;
    }

    @Override
    public byte getByte(int columnIndex) {
        return 0;
    }

    @Override
    public short getShort(int columnIndex) {
        return 0;
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return getObject(columnIndex, Integer.class);
    }

    @Override
    public long getLong(int columnIndex) {
        return 0;
    }

    @Override
    public float getFloat(int columnIndex) {
        return 0;
    }

    @Override
    public double getDouble(int columnIndex) {
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) {
        return null;
    }

    @Override
    public byte[] getBytes(int columnIndex) {
        return new byte[0];
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return getObject(columnIndex, Date.class);
    }

    /**
     * Unsupported. Use {@link #getDate(int)}}
     */
    @Override
    public Time getTime(int columnIndex) {
        return null;
    }

    /**
     * Unsupported Use {@link #getDate(int)}
     */
    @Override
    public Timestamp getTimestamp(int columnIndex) {
        return null;
    }

    /**
     * Unsupported
     */
    @Override
    public InputStream getAsciiStream(int columnIndex) {
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) {
        return null;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) {
        return null;
    }

    @Override
    public String getString(String columnLabel) {
        return "";
    }

    @Override
    public boolean getBoolean(String columnLabel) {
        return false;
    }

    @Override
    public byte getByte(String columnLabel) {
        return 0;
    }

    @Override
    public short getShort(String columnLabel) {
        return 0;
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public long getLong(String columnLabel) {
        return 0;
    }

    @Override
    public float getFloat(String columnLabel) {
        return 0;
    }

    @Override
    public double getDouble(String columnLabel) {
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) {
        return null;
    }

    @Override
    public byte[] getBytes(String columnLabel) {
        return new byte[0];
    }

    @Override
    public Date getDate(String columnLabel) {
        return null;
    }

    @Override
    public Time getTime(String columnLabel) {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) {
        return null;
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) {
        return null;
    }

    /**
     * Unsupported
     */
    @Override
    public InputStream getUnicodeStream(String columnLabel) {
        return null;
    }

    /**
     * Unsupported
     */
    @Override
    public InputStream getBinaryStream(String columnLabel) {
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() {
    }

    /**
     * Unsupported
     */
    @Override
    public String getCursorName() {
        return "";
    }

    /**
     * Unsupported
     */
    @Override
    public ResultSetMetaData getMetaData() {
        return null;
    }

    /**
     * Returns WinSearchStatement extends {@link Statement} object
     *
     * @return {@link WinSearchStatement}
     * @throws SQLException when called on a closed result set
     */
    @Override
    public Statement getStatement() throws SQLException {
        assertClosedResultSet();
        return statement;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return currentRow.getCellObject(columnIndex);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return currentRow.getCellObject(columnIndex, type);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public int findColumn(String columnLabel) {
        return 0;
    }


    @Override
    public Reader getCharacterStream(int columnIndex) {
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) {
        return null;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return comInvokeExWrap(recordset::isBOF);
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return comInvokeExWrap(recordset::isEOF);
    }

    /**
     * Unsupported
     */
    @Override
    public boolean isFirst() {
        return false;
    }

    /**
     * Unsupported
     */
    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public void beforeFirst() throws SQLException {
        if (isEmpty()) return;
        comInvokeExWrap(recordset::moveFirst);
        comInvokeExWrap(recordset::movePrevious);
        currentRow.setRow(null);
    }

    /**
     * Unsupported
     */
    @Override
    public void afterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * Moves the cursor to the first row in this ResultSet object
     *
     * @return true if absolute position is 1
     * @throws SQLException when ResultSet is closed
     */
    @Override
    public boolean first() throws SQLException {
        assertClosedResultSet();
        recordset.moveFirst();
        return recordset.absolutePosition() == 1;
    }

    /**
     * Unsupported
     */
    @Override
    public boolean last() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * Unsupported. Retrieves the current row number
     *
     * @return the current row number; 0 if there is no current row
     * @throws SQLException called on a closed result set
     */
    @Override
    public int getRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return false;
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return false;
    }

    @Override
    public boolean previous() throws SQLException {
        assertClosedResultSet();
        recordset.movePrevious();
        return getRow() > 0 || isBeforeFirst();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getType() {
        return TYPE_SCROLL_INSENSITIVE;
    }

    @Override
    public int getConcurrency() {
        return CONCUR_READ_ONLY;
    }

    /**
     * Unsupported
     */
    @Override
    public boolean rowUpdated() {
        return false;
    }

    @Override
    public boolean rowInserted() {
        return false;
    }

    @Override
    public boolean rowDeleted() {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) {
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) {
    }

    @Override
    public void updateByte(int columnIndex, byte x) {
    }

    @Override
    public void updateShort(int columnIndex, short x) {
    }

    @Override
    public void updateInt(int columnIndex, int x) {
    }

    @Override
    public void updateLong(int columnIndex, long x) {
    }

    @Override
    public void updateFloat(int columnIndex, float x) {
    }

    @Override
    public void updateDouble(int columnIndex, double x) {
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) {
    }

    @Override
    public void updateString(int columnIndex, String x) {
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) {
    }

    @Override
    public void updateDate(int columnIndex, Date x) {
    }

    @Override
    public void updateTime(int columnIndex, Time x) {
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) {
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) {
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) {
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) {
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) {
    }

    @Override
    public void updateObject(int columnIndex, Object x) {
    }

    @Override
    public void updateNull(String columnLabel) {
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) {
    }

    @Override
    public void updateByte(String columnLabel, byte x) {

    }

    @Override
    public void updateShort(String columnLabel, short x) {
    }

    @Override
    public void updateInt(String columnLabel, int x) {
    }

    @Override
    public void updateLong(String columnLabel, long x) {
    }

    @Override
    public void updateFloat(String columnLabel, float x) {
    }

    @Override
    public void updateDouble(String columnLabel, double x) {
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) {
    }

    @Override
    public void updateString(String columnLabel, String x) {
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) {

    }

    @Override
    public void updateDate(String columnLabel, Date x) {
    }

    @Override
    public void updateTime(String columnLabel, Time x) {
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) {
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) {
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) {
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) {
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) {
    }

    @Override
    public void updateObject(String columnLabel, Object x) {
    }

    @Override
    public void insertRow() {
    }

    @Override
    public void updateRow() {

    }

    @Override
    public void deleteRow() {

    }

    @Override
    public void refreshRow() {

    }

    @Override
    public void cancelRowUpdates() {

    }

    /**
     * Unsupported
     */
    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * Unsupported
     */
    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Ref getRef(int columnIndex) {
        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) {
        return null;
    }

    @Override
    public Clob getClob(int columnIndex) {
        return null;
    }

    @Override
    public Array getArray(int columnIndex) {
        return null;
    }

    @Override
    public Ref getRef(String columnLabel) {
        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) {
        return null;
    }

    @Override
    public Clob getClob(String columnLabel) {
        return null;
    }

    @Override
    public Array getArray(String columnLabel) {
        return null;
    }

    /**
     * Unsupported
     */
    @Override
    public Date getDate(int columnIndex, Calendar cal) {
        return null;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) {
        return null;
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) {
        return null;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) {
        return null;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) {
        return null;
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) {
    }

    @Override
    public void updateRef(String columnLabel, Ref x) {
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) {
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) {
    }

    @Override
    public void updateClob(int columnIndex, Clob x) {
    }

    @Override
    public void updateClob(String columnLabel, Clob x) {
    }

    @Override
    public void updateArray(int columnIndex, Array x) {
    }

    @Override
    public void updateArray(String columnLabel, Array x) {
    }

    @Override
    public RowId getRowId(int columnIndex) {
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) {
        return null;
    }

    /**
     * Unsupported
     */
    @SuppressWarnings("MagicConstant")
    @Override
    public int getHoldability() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() throws SQLException {
        return comInvokeExWrap(() -> ObjectStateEnum.valueOf(recordset.state()) == ObjectStateEnum.adStateClosed);
    }

    @Override
    public void updateNString(int columnIndex, String nString) {
    }

    @Override
    public void updateNString(String columnLabel, String nString) {
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) {
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) {
    }

    @Override
    public NClob getNClob(int columnIndex) {
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) {
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) {
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) {
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) {
    }

    @Override
    public String getNString(int columnIndex) {
        return "";
    }

    @Override
    public String getNString(String columnLabel) {
        return "";
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) {
        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) {
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) {
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) {
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) {
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) {
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) {
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) {
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) {
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) {

    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) {
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) {
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) {
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader) {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader) {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) {

    }


    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return null;
    }

    /**
     * Unwrap {@link ADORecordset} object
     *
     * @param iface {@link ADORecordset}.class
     * @param <T>   {@link ADORecordset}
     * @return proxy COM object of {@link ADORecordset}
     * @throws SQLException If no object found that implements the interface
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) return (T) recordset;
        throw new WinSearchSQLException("No object found that implements the interface " + iface);
    }

    /**
     * Returns true for {@link ADORecordset}.class
     *
     * @param iface {@link ADORecordset}.class
     * @return true for {@link ADORecordset}.class
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return Arrays.stream(recordset.getClass().getInterfaces())
                .anyMatch(i -> i == iface);
    }

    /**
     * {@inheritDoc}
     * The ResultSet rests opened after stream processing
     *
     * @return {@link Stream Stream&lt;WinSearchResultSet&gt;} | empty {@link Stream} if an exception was thrown
     */
    @Override
    public Stream<WinSearchResultSet> stream() {
        return streamOf(this);
    }

    /**
     * Stream of {@link WinSearchResultSet}
     *
     * @param closeObjects Objects with implemented interface {@link AutoCloseable} that should be closed after stream processing
     * @return {@link Stream Stream&lt;WinSearchResultSet&gt;} | empty {@link Stream} if an exception was thrown
     */
    public Stream<WinSearchResultSet> stream(AutoCloseable... closeObjects) {
        return streamOf(this, closeObjects);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptional(int columnIndex) {
        try {
            return (Optional<T>) Optional.of(getObject(columnIndex));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    interface ThrowingSupplier<T, E extends Exception> {
        /**
         * Implementation of {@link java.util.function.Supplier} that throws exception
         * @return supplier result
         * @throws E supplier exception
         */
        T get() throws E;
    }

    /**
     * Throwing wrapper
     *
     * @param supplier with {@link COMInvokeException}
     * @param <T>      type of value
     * @return value from supplier
     * @throws WinSearchSQLException instead {@link COMInvokeException}
     */
    private static <T> T comInvokeExWrap(final ThrowingSupplier<T, COMInvokeException> supplier) throws WinSearchSQLException {
        try {
            return supplier.get();
        } catch (COMInvokeException e) {
            throw new WinSearchSQLException(e);
        }
    }

    @FunctionalInterface
    interface ThrowingSupplierVoid<E extends Exception> {
        /**
         * Implementation of {@link java.util.function.Supplier} that throws exception
         * @throws E supplier exception
         */
        void get() throws E;
    }

    /**
     * Throwing wrapper
     *
     * @param supplier with {@link COMInvokeException}
     * @throws WinSearchSQLException instead {@link COMInvokeException}
     */
    private static void comInvokeExWrap(final ThrowingSupplierVoid<COMInvokeException> supplier) throws WinSearchSQLException {
        try {
            supplier.get();
        } catch (COMInvokeException e) {
            throw new WinSearchSQLException(e);
        }
    }

    private class CurrentRow {


        private Object[] row = null;

        public void setRow(Object[] row) {
            this.row = row;
        }

        public Object getCellObject(int columnIndex) throws SQLException {
            if (row == null) assertReadingOutOfBounds();
            assertIndex(columnIndex);
            return row[columnIndex];
        }

        @SuppressWarnings("unchecked")
        public <T> T getCellObject(int columnIndex, Class<T> type) throws SQLException {
            Object obj = getCellObject(columnIndex);
            if (obj.getClass() == type) return (T) obj;
            if (type == Date.class && obj instanceof java.util.Date date) return (T) new Date(date.getTime());
            throw new IllegalStateException("Unexpected type: " + obj.getClass() + " for column " + columnIndex);
        }

        private void assertIndex(int columnIndex) throws WinSearchSQLException {
            if (columnIndex >= row.length)
                throw new WinSearchSQLException("Index " + columnIndex + " out of bounds for length of record " + row.length);
        }

    }
}
