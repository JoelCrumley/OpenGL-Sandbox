package joel.opengl.database;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDatabase {

    public SQLiteDatabase(String databaseFilePath) {
        this.databaseFilePath = databaseFilePath;
        init();
    }

    private File databaseFile;
    private String databaseFilePath;
    private volatile Connection connection;

    public void init() {

        databaseFile = new File(databaseFilePath);
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Could not create DB file!");
                System.err.println();
            }
        }

        try {

            openConnection();

            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "name TEXT NOT NULL," +
                            "password BLOB NOT NULL" +
                            ");"
            );

            ps.execute();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Exception while opening SQLite connection.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return getConnection(true);
    }

    public Connection getConnection(boolean openIfClosed) {
        try {
            if (openIfClosed && (connection == null || connection.isClosed())) openConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public synchronized void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch (SQLException e) {
            System.err.println("Exception while opening SQLite connection " + databaseFilePath);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Couldn't load SQLite driver: org.sqlite.JDBC");
            e.printStackTrace();
        }
    }

    public synchronized void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Exception while closing SQLite connection " + databaseFilePath);
            e.printStackTrace();
        }
    }

}
