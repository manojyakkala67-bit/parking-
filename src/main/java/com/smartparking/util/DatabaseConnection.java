package com.smartparking.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Singleton / Utility class managing JDBC Database Connections to MySQL.
 * Prevents hardcoded credentials and supports runtime configuration.
 */
public final class DatabaseConnection {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String CONFIG_FILE_PATH = "config/db.properties";

    private static String host = "localhost";
    private static String port = "3306";
    private static String database = "smart_parking_db";
    private static String username = "root";
    private static String password = "root";
    private static boolean useSSL = false;
    private static String serverTimezone = "UTC";
    private static boolean allowPublicKeyRetrieval = true;

    private static boolean driverLoaded = false;

    static {
        loadConfiguration();
        ensureDriver();
    }

    private DatabaseConnection() {
    }

    private static synchronized void ensureDriver() {
        if (!driverLoaded) {
            try {
                Class.forName(DRIVER_CLASS);
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found on classpath: " + e.getMessage());
            }
        }
    }

    /**
     * Loads configuration from config/db.properties, then classpath db.properties, then env vars.
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();

        // 1. Try external config file
        File extFile = new File(CONFIG_FILE_PATH);
        if (extFile.exists()) {
            try (FileInputStream fis = new FileInputStream(extFile)) {
                props.load(fis);
            } catch (IOException ignored) {
            }
        }

        // 2. Try classpath resource if external didn't load
        if (props.isEmpty()) {
            try (InputStream is = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
                if (is != null) {
                    props.load(is);
                }
            } catch (IOException ignored) {
            }
        }

        // 3. Fallbacks and Environment variable overrides
        host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : props.getProperty("db.host", host);
        port = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : props.getProperty("db.port", port);
        database = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : props.getProperty("db.name", database);
        username = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : props.getProperty("db.username", username);
        password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : props.getProperty("db.password", password);
        useSSL = Boolean.parseBoolean(props.getProperty("db.useSSL", String.valueOf(useSSL)));
        serverTimezone = props.getProperty("db.serverTimezone", serverTimezone);
        allowPublicKeyRetrieval = Boolean.parseBoolean(props.getProperty("db.allowPublicKeyRetrieval", "true"));
    }

    /**
     * Saves user-modified configuration to external config file config/db.properties.
     */
    public static synchronized void saveConfiguration(String newHost, String newPort, String newDb, 
                                                      String newUser, String newPass) throws IOException {
        host = (newHost != null && !newHost.trim().isEmpty()) ? newHost.trim() : "localhost";
        port = (newPort != null && !newPort.trim().isEmpty()) ? newPort.trim() : "3306";
        database = (newDb != null && !newDb.trim().isEmpty()) ? newDb.trim() : "smart_parking_db";
        username = (newUser != null && !newUser.trim().isEmpty()) ? newUser.trim() : "root";
        password = (newPass != null) ? newPass : "";

        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", port);
        props.setProperty("db.name", database);
        props.setProperty("db.username", username);
        props.setProperty("db.password", password);
        props.setProperty("db.useSSL", String.valueOf(useSSL));
        props.setProperty("db.serverTimezone", serverTimezone);
        props.setProperty("db.allowPublicKeyRetrieval", String.valueOf(allowPublicKeyRetrieval));

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE_PATH)) {
            props.store(fos, "Smart Parking Management System Database Configuration");
        }
    }

    public static String buildJdbcUrl(boolean includeDatabase) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://").append(host).append(":").append(port);
        if (includeDatabase && database != null && !database.trim().isEmpty()) {
            sb.append("/").append(database);
        }
        sb.append("?useSSL=").append(useSSL)
          .append("&allowPublicKeyRetrieval=").append(allowPublicKeyRetrieval)
          .append("&serverTimezone=").append(serverTimezone)
          .append("&connectTimeout=5000")
          .append("&characterEncoding=UTF-8");
        return sb.toString();
    }

    /**
     * Obtains a new database connection.
     */
    public static Connection getConnection() throws SQLException {
        ensureDriver();
        String url = buildJdbcUrl(true);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Obtains a connection to MySQL server without selecting a specific database.
     * Useful for executing database creation scripts.
     */
    public static Connection getServerConnection() throws SQLException {
        ensureDriver();
        String url = buildJdbcUrl(false);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Tests database connectivity.
     * @return null if successful, or error message string if failed.
     */
    public static String testConnection() {
        try (Connection conn = getConnection()) {
            if (conn.isValid(3)) {
                return null; // Connection valid
            }
            return "Connection established but validation timed out.";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    /**
     * Initializes the database schema by executing schema.sql statements.
     */
    public static void initializeDatabaseSchema() throws SQLException, IOException {
        ensureDriver();
        InputStream is = DatabaseConnection.class.getResourceAsStream("/database/schema.sql");
        if (is == null) {
            File f = new File("database.sql");
            if (f.exists()) {
                is = new FileInputStream(f);
            }
        }
        if (is == null) {
            throw new IOException("schema.sql or database.sql not found in resources or project root.");
        }

        StringBuilder sqlBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.startsWith("//") || line.isEmpty()) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
            }
        }

        // Split statements by semicolon
        String[] statements = sqlBuilder.toString().split(";\\s*(\\r?\\n)+");

        try (Connection serverConn = getServerConnection();
             Statement stmt = serverConn.createStatement()) {
            for (String sql : statements) {
                String cleanSql = sql.trim();
                if (!cleanSql.isEmpty()) {
                    try {
                        stmt.execute(cleanSql);
                    } catch (SQLException ex) {
                        // Ignore already exists error or print warning
                        System.err.println("Executing SQL warning/error: " + ex.getMessage() + "\nSQL: " + cleanSql);
                    }
                }
            }
        }
    }

    // Getters for configuration dialog
    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDatabase() { return database; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}
