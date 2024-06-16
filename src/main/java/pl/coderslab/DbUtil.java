package pl.coderslab;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {

    public static Connection connect() throws SQLException {
        Properties dbConfig = new Properties();

        try (FileReader dbDetails = new FileReader("db_config.properties")) {
            dbConfig.load(dbDetails);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final String HOST = dbConfig.getProperty("HOST");
        final String PORT = dbConfig.getProperty("PORT");
        final String USE_SSL = dbConfig.getProperty("USE_SSL");
        final String CHARACTER_ENCODING = dbConfig.getProperty("CHARACTER_ENCODING");
        final String SERVER_TIMEZONE = dbConfig.getProperty("SERVER_TIMEZONE");
        final String ALLOW_PUBLIC_KEY_RETRIEVAL = dbConfig.getProperty("ALLOW_PUBLIC_KEY_RETRIEVAL");
        final String DB_NAME = dbConfig.getProperty("DB_NAME");
        final String DB_USER = dbConfig.getProperty("DB_USER");
        final String DB_PASSWORD = dbConfig.getProperty("DB_PASSWORD");

        final String DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/"
                + DB_NAME + "?useSSL=" + USE_SSL + "&characterEncoding=" + CHARACTER_ENCODING
                + "&serverTimezone=" + SERVER_TIMEZONE + "&allowPublicKeyRetrieval=" + ALLOW_PUBLIC_KEY_RETRIEVAL;

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
