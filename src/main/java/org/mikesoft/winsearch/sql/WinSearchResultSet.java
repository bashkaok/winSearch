package org.mikesoft.winsearch.sql;

import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.OaIdl;
import org.mikesoft.winsearch.ado.ObjectStateEnum;
import org.mikesoft.winsearch.ado.ADORecordset;
import org.mikesoft.winsearch.utils.OaIdlUtil;
import org.mikesoft.winsearch.utils.ThrowingSupplier;
import org.mikesoft.winsearch.utils.ThrowingSupplierVoid;

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
 * Wrapper for {@link ADORecordset RecordSet(ADO)}
 */
public class WinSearchResultSet implements ResultSet {
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
     * Returns number of records in ResultSet. ResultSet still is valid
     *
     * @return count of records
     * @throws WinSearchSQLException when ResultSet is closed
     */
    public long size() throws SQLException {
        assertClosedResultSet();
        return comInvokeExWrap(recordset::getRecordCount);
    }

    /**
     * @throws SQLException when ResultSet is closed
     */
    public boolean isEmpty() throws SQLException {
        return comInvokeExWrap(() -> recordset.isBOF() && recordset.isEOF());
    }

    private void setCurrentRecord() throws SQLException {
        readingAssert();
        OaIdl.SAFEARRAY ar = recordset.getRows(1);
        Object[][] record = (Object[][]) OaIdlUtil.toPrimitiveArray(ar, true);
        currentRow.setRow(record[0]);
    }

    private void readingAssert() throws SQLException {
        if (isBeforeFirst()) throw new WinSearchSQLException("Try reading before first record");
        if (isAfterLast()) throw new WinSearchSQLException("Try reading after last record");
    }

    private void assertClosedResultSet() throws SQLException {
        if (isClosed()) throw new WinSearchSQLException("ResultSet is closed");
    }

    @Override
    public boolean next() throws SQLException {
        try {
            if (recordset.isEOF()) return false;
            if (isEmpty()) return false;
            if (isBeforeFirst()) recordset.moveNext();
            setCurrentRecord();
            return true;
        } catch (COMInvokeException e) {
            throw new WinSearchSQLException(e);
        }
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

    @Override
    public Statement getStatement() throws SQLException {
        assertClosedResultSet();
        return statement;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return currentRow.getObject(columnIndex);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return currentRow.getObject(columnIndex, type);
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
        try {
            return recordset.isBOF();
        } catch (COMInvokeException e) {
            throw new WinSearchSQLException(e);
        }
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
        try {
            recordset.moveFirst();
            recordset.move(-1);
            currentRow.setRow(null);
        } catch (COMInvokeException e) {
            throw new WinSearchSQLException(e);
        }
    }

    /**
    * Unsupported
    */
    @Override
    public void afterLast() {
    }

    @Override
    public boolean first() throws SQLException {
        return false;
    }

    @Override
    public boolean last() throws SQLException {
        return false;
    }

    /**
    * Unsupported
    */
    @Override
    public int getRow() {
        return 0;
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
        return false;
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
        return TYPE_FORWARD_ONLY;
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

    @Override
    public void moveToInsertRow() {

    }

    /**
     * Unsupported
     */
    @Override
    public void moveToCurrentRow() {

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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) return (T) recordset;
        throw new WinSearchSQLException("No object found that implements the interface " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return Arrays.stream(recordset.getClass().getInterfaces())
                .anyMatch(i -> i == iface);
    }

    /**
     * Stream of ResultSet. The ResultSet rests opened after stream processing.
     */
    public Stream<ResultSet> stream() throws SQLException {
        ResultSet resultSet = this;
        resultSet.beforeFirst();
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE, Spliterator.IMMUTABLE) {
            @Override
            public boolean tryAdvance(Consumer<? super ResultSet> action) {
                try {
                    if (!resultSet.next()) {
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                action.accept(resultSet);
                return true;
            }
        }, false);
    }

    public static <T> T comInvokeExWrap(final ThrowingSupplier<T, COMInvokeException> supplier) throws WinSearchSQLException {
        try {
            return supplier.get();
        } catch (COMInvokeException e) {
            throw new WinSearchSQLException(e);
        }
    }

    public static void comInvokeExWrap(final ThrowingSupplierVoid<COMInvokeException> supplier) throws WinSearchSQLException {
        try {
            supplier.get();
        } catch (COMInvokeException e) {
            throw new WinSearchSQLException(e);
        }
    }

    class CurrentRow {
        private Object[] row = null;

        public Object[] getRow() {
            return row;
        }

        public void setRow(Object[] row) {
            this.row = row;
        }

        public Object getObject(int columnIndex) throws SQLException {
            if (row == null) readingAssert();
            assertIndex(columnIndex);
            return row[columnIndex];
        }

        @SuppressWarnings("unchecked")
        public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
            Object obj = getObject(columnIndex);
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
