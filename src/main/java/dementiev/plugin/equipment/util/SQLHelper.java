package dementiev.plugin.equipment.util;

import com.atlassian.jira.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 * @author dmitry dementiev
 */

public class SQLHelper {
    public final static ResourceBundle SQL = ResourceBundle.getBundle("plugin");
    private static Connection connection;
//    private ConnectionPool pool;

    public static Connection getConnection() throws DataAccessException, SQLException, ClassNotFoundException {
        String driver = getSQLProperty("db.driver");
        String url = getSQLProperty("db.url");
        String login = getSQLProperty("db.login");
        String pass = getSQLProperty("db.pass");
        Class.forName(driver);
        /*   if (pool == null) {
            pool = new ConnectionPool("jiraPool", 10,// <maxpool>
                    30,// <maxconn>
                    180,// <expiry> milliseconds
                    url, login, pass);
            long timeout = 2000; // 2 second timeout
            connection = pool.getConnection(timeout);
        }*/
        if (connection == null) {
            connection = DriverManager.getConnection(url, login, pass);
        }
        return connection;
    }

    public static void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    public static String getSQLProperty(String key) {
        return SQL.getString(key);
    }
}