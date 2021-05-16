package nl.adrianmensing.krokodil.database.service.mysql;

import nl.adrianmensing.krokodil.database.service.DatabaseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabaseService implements DatabaseService {
    private static final String USERNAME = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASS");
    private static final String DATABASE = "krokodil_db";
    private static Connection connection = null;

    /**
     * Returns a {@link Connection} instance to the MySQL database. Note that this is
     * executed in a synchronized fashion, where we only have a singleton instance of the
     * to be returned connection. This might work for development purposes, but a connection pool
     * should be preferred when going into production.
     *
     * @return A connection to the MySQL database.
     * @throws SQLException If a database access error occurs or the url is `null`
     */
    public static synchronized Connection getConnection() throws SQLException {
        // TODO: Rewrite this class to use a connection pool, rather than a singleton instance.
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE, USERNAME, PASSWORD);
        }

        return connection;
    }
}
