package com.example.stockvisualiser.database;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * DatabaseManager - Singleton class for managing SQLite database connection and operations
 * Implements the Singleton pattern to ensure only one database connection exists
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:stockvisualiser.db";

    /**
     * Private constructor to prevent instantiation
     * Initializes database connection and creates tables
     */
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the singleton instance of DatabaseManager
     * @return DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Get the database connection
     * @return Connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Create all necessary database tables
     */
    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Users table with role-based access
            String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL CHECK(role IN ('ADMIN', 'TRADER')),
                    created_at TEXT NOT NULL,
                    last_login TEXT
                );
            """;

            // Stocks table for storing stock information
            String stocksTable = """
                CREATE TABLE IF NOT EXISTS stocks (
                    stock_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    symbol TEXT UNIQUE NOT NULL,
                    company_name TEXT NOT NULL,
                    sector TEXT,
                    current_price REAL NOT NULL,
                    last_updated TEXT NOT NULL
                );
            """;

            // Portfolio table for user's stock holdings
            String portfolioTable = """
                CREATE TABLE IF NOT EXISTS portfolio (
                    portfolio_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    stock_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    purchase_price REAL NOT NULL,
                    purchase_date TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                    FOREIGN KEY (stock_id) REFERENCES stocks(stock_id) ON DELETE CASCADE
                );
            """;

            // Transactions table for buy/sell history
            String transactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    stock_id INTEGER NOT NULL,
                    transaction_type TEXT NOT NULL CHECK(transaction_type IN ('BUY', 'SELL')),
                    quantity INTEGER NOT NULL,
                    price_per_share REAL NOT NULL,
                    total_amount REAL NOT NULL,
                    transaction_date TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                    FOREIGN KEY (stock_id) REFERENCES stocks(stock_id) ON DELETE CASCADE
                );
            """;

            // Watchlist table for stocks user wants to monitor
            String watchlistTable = """
                CREATE TABLE IF NOT EXISTS watchlist (
                    watchlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    stock_id INTEGER NOT NULL,
                    added_date TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                    FOREIGN KEY (stock_id) REFERENCES stocks(stock_id) ON DELETE CASCADE,
                    UNIQUE(user_id, stock_id)
                );
            """;

            // Execute table creation
            stmt.execute(usersTable);
            stmt.execute(stocksTable);
            stmt.execute(portfolioTable);
            stmt.execute(transactionsTable);
            stmt.execute(watchlistTable);

            // Insert default admin user and sample stocks
            insertDefaultData();

            stmt.close();
            System.out.println("Database tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insert default data for testing and demonstration
     */
    private void insertDefaultData() {
        try {
            // Check if admin exists
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            PreparedStatement checkStmt = connection.prepareStatement(checkAdmin);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert default admin user (password: admin123)
                String adminHash = org.mindrot.jbcrypt.BCrypt.hashpw("admin123", org.mindrot.jbcrypt.BCrypt.gensalt());
                String insertAdmin = """
                    INSERT INTO users (username, password_hash, email, full_name, role, created_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;
                PreparedStatement pstmt = connection.prepareStatement(insertAdmin);
                pstmt.setString(1, "admin");
                pstmt.setString(2, adminHash);
                pstmt.setString(3, "admin@stockvisualiser.com");
                pstmt.setString(4, "System Administrator");
                pstmt.setString(5, "ADMIN");
                pstmt.setString(6, LocalDateTime.now().toString());
                pstmt.executeUpdate();
                pstmt.close();
                
                // Insert demo trader user (password: demo123)
                String demoHash = org.mindrot.jbcrypt.BCrypt.hashpw("demo123", org.mindrot.jbcrypt.BCrypt.gensalt());
                String insertDemo = """
                    INSERT INTO users (username, password_hash, email, full_name, role, created_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;
                pstmt = connection.prepareStatement(insertDemo);
                pstmt.setString(1, "demo");
                pstmt.setString(2, demoHash);
                pstmt.setString(3, "demo@stockvisualiser.com");
                pstmt.setString(4, "Demo Trader");
                pstmt.setString(5, "TRADER");
                pstmt.setString(6, LocalDateTime.now().toString());
                pstmt.executeUpdate();
                pstmt.close();
            }
            rs.close();
            checkStmt.close();

            // Check if stocks exist
            String checkStocks = "SELECT COUNT(*) FROM stocks";
            checkStmt = connection.prepareStatement(checkStocks);
            rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert sample stocks
                String[][] sampleStocks = {
                    {"AAPL", "Apple Inc.", "Technology", "175.50"},
                    {"GOOGL", "Alphabet Inc.", "Technology", "140.25"},
                    {"MSFT", "Microsoft Corporation", "Technology", "380.75"},
                    {"AMZN", "Amazon.com Inc.", "Consumer Cyclical", "145.30"},
                    {"TSLA", "Tesla Inc.", "Automotive", "242.80"},
                    {"META", "Meta Platforms Inc.", "Technology", "330.45"},
                    {"NVDA", "NVIDIA Corporation", "Technology", "495.20"},
                    {"JPM", "JPMorgan Chase & Co.", "Financial", "155.60"},
                    {"V", "Visa Inc.", "Financial", "245.90"},
                    {"WMT", "Walmart Inc.", "Consumer Defensive", "165.30"}
                };

                String insertStock = """
                    INSERT INTO stocks (symbol, company_name, sector, current_price, last_updated)
                    VALUES (?, ?, ?, ?, ?)
                """;
                
                for (String[] stock : sampleStocks) {
                    PreparedStatement pstmt = connection.prepareStatement(insertStock);
                    pstmt.setString(1, stock[0]);
                    pstmt.setString(2, stock[1]);
                    pstmt.setString(3, stock[2]);
                    pstmt.setDouble(4, Double.parseDouble(stock[3]));
                    pstmt.setString(5, LocalDateTime.now().toString());
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }
            rs.close();
            checkStmt.close();

            System.out.println("Default data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting default data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
