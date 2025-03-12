package org.mikesoft.winsearch.sql;

import org.mikesoft.winsearch.ado.ADOConnection;
import org.mikesoft.winsearch.ado.COMFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Implementation of {@link DataSource} for Windows Index Search
 */
public class WinSearchDataSource implements DataSource {
    public static final String CONNECTION_STR = "Provider=Search.CollatorDSO;Extended Properties='Application=Windows';";

    /**
     * Public no-arg constructor according to {@link DataSource}
     */
    public WinSearchDataSource(){}

    /**
     * Creates and opens native COM ADO connection with Search.CollatorDSO provider {@link #CONNECTION_STR}
     * @return opened {@link ADOConnection} object
     */
    public static ADOConnection newADOConnection() {
        ADOConnection adoConnection = COMFactory.newADOConnection();
        adoConnection.open(CONNECTION_STR, "", "", -1);
        return adoConnection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new WinSearchConnection(newADOConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {

    }

    @Override
    public void setLoginTimeout(int seconds) {

    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
