package org.mikesoft.winsearch;

import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.OaIdl;
import org.mikesoft.winsearch.ado.ObjectStateEnum;
import org.mikesoft.winsearch.ado.Recordset;
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

public class ResultSetImpl implements ResultSet {
    private final Recordset recordset;
    private final Statement statement;
    private final CurrentRow currentRow = new CurrentRow();

    public ResultSetImpl(Recordset recordset) {
        this(recordset, null);
    }

    public ResultSetImpl(Recordset recordset, Statement statement) {
        this.recordset = recordset;
        this.statement = statement;
    }


    /**
     * Returns number of records in ResultSet. ResultSet still is valid
     */
    public long size() throws SQLException {
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
        if (isBeforeFirst()) throw new SearchSQLException("Try reading before first record");
        if (isAfterLast()) throw new SearchSQLException("Try reading after last record");
    }

    private void closedAssert() throws SQLException {
        if (isClosed()) throw new SearchSQLException("ResultSet is closed");
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
            throw new SearchSQLException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) return;
        comInvokeExWrap(recordset::close);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return false;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return getObject(columnIndex, String.class);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return false;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return 0;
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return 0;
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return getObject(columnIndex, Integer.class);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return 0;
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return 0;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return null;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
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

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return "";
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return false;
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return null;
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return new byte[0];
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) {
        return null;
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return null;
    }

    /**
     * Unsupported
     */
    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return null;
    }

    /**
     * Unsupported
     */
    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

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
        closedAssert();
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
    public int findColumn(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        try {
            return recordset.isBOF();
        } catch (COMInvokeException e) {
            throw new SearchSQLException(e);
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
            throw new SearchSQLException(e);
        }
    }

    @Override
    public void afterLast() throws SQLException {

    }

    @Override
    public boolean first() throws SQLException {
        return false;
    }

    @Override
    public boolean last() throws SQLException {
        return false;
    }

    @Override
    public int getRow() throws SQLException {
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
    public void updateNull(int columnIndex) throws SQLException {

    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {

    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {

    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {

    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {

    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {

    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {

    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {

    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {

    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {

    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {

    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {

    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {

    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {

    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {

    }

    @Override
    public void updateLong(String columnLabel, long x) {
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {

    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {

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
    public void updateDate(String columnLabel, Date x) throws SQLException {

    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) {
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {

    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {

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

    @Override
    public void moveToCurrentRow() throws SQLException {

    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
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
    public void updateRef(int columnIndex, Ref x) throws SQLException {

    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {

    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {

    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {

    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return null;
    }

    /**
     * Unsupported
     */
    @SuppressWarnings("MagicConstant")
    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

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
    public NClob getNClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) {
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) {
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return "";
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return "";
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
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

    /**
     * Unsupported
     */
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
        throw new SearchSQLException("No object found that implements the interface " + iface);
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

    public static <T> T comInvokeExWrap(final ThrowingSupplier<T, COMInvokeException> supplier) throws SearchSQLException {
        try {
            return supplier.get();
        } catch (COMInvokeException e) {
            throw new SearchSQLException(e);
        }
    }

    public static void comInvokeExWrap(final ThrowingSupplierVoid<COMInvokeException> supplier) throws SearchSQLException {
        try {
            supplier.get();
        } catch (COMInvokeException e) {
            throw new SearchSQLException(e);
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

        public String getString(int columnIndex) throws SQLException {
            if (getObject(columnIndex) instanceof String str) return str;
            throw new IllegalStateException("Unexpected type: " + getObject(columnIndex).getClass() + " for column " + columnIndex);
        }

        private void assertIndex(int columnIndex) throws SearchSQLException {
            if (columnIndex >= row.length)
                throw new SearchSQLException("Index " + columnIndex + " out of bounds for length of record " + row.length);
        }
    }

}
