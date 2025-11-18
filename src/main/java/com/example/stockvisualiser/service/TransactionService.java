package com.example.stockvisualiser.service;

import com.example.stockvisualiser.database.DatabaseManager;
import com.example.stockvisualiser.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * TransactionService - Handles buy/sell transactions
 */
public class TransactionService {
    private final DatabaseManager dbManager;
    private final PortfolioService portfolioService;

    public TransactionService() {
        this.dbManager = DatabaseManager.getInstance();
        this.portfolioService = new PortfolioService();
    }

    /**
     * Record a buy transaction
     */
    public boolean buyStock(int userId, int stockId, int quantity, double pricePerShare) {
        Connection conn = dbManager.getConnection();
        
        try {
            // Start transaction
            conn.setAutoCommit(false);

            // Record transaction
            String transactionQuery = """
                INSERT INTO transactions (user_id, stock_id, transaction_type, quantity, price_per_share, total_amount, transaction_date)
                VALUES (?, ?, 'BUY', ?, ?, ?, ?)
            """;
            
            double totalAmount = quantity * pricePerShare;
            PreparedStatement pstmt = conn.prepareStatement(transactionQuery);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stockId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, pricePerShare);
            pstmt.setDouble(5, totalAmount);
            pstmt.setString(6, LocalDateTime.now().toString());
            pstmt.executeUpdate();
            pstmt.close();

            // Check if user already owns this stock
            String checkQuery = "SELECT portfolio_id, quantity, purchase_price FROM portfolio WHERE user_id = ? AND stock_id = ?";
            pstmt = conn.prepareStatement(checkQuery);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stockId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Update existing holding with weighted average price
                int existingQuantity = rs.getInt("quantity");
                double existingPrice = rs.getDouble("purchase_price");
                int portfolioId = rs.getInt("portfolio_id");
                
                int newTotalQuantity = existingQuantity + quantity;
                double newAvgPrice = ((existingQuantity * existingPrice) + (quantity * pricePerShare)) / newTotalQuantity;
                
                String updateQuery = "UPDATE portfolio SET quantity = ?, purchase_price = ? WHERE portfolio_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, newTotalQuantity);
                updateStmt.setDouble(2, newAvgPrice);
                updateStmt.setInt(3, portfolioId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // Add new holding to portfolio
                portfolioService.addToPortfolio(userId, stockId, quantity, pricePerShare, java.time.LocalDate.now());
            }

            rs.close();
            pstmt.close();

            // Commit transaction
            conn.commit();
            conn.setAutoCommit(true);
            
            System.out.println("Buy transaction successful");
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
            System.err.println("Error processing buy transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Record a sell transaction
     */
    public boolean sellStock(int userId, int stockId, int quantity, double pricePerShare) {
        Connection conn = dbManager.getConnection();
        
        try {
            // Start transaction
            conn.setAutoCommit(false);

            // Check if user has enough shares to sell
            String checkQuery = "SELECT portfolio_id, quantity FROM portfolio WHERE user_id = ? AND stock_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(checkQuery);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stockId);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.err.println("User does not own this stock");
                conn.rollback();
                conn.setAutoCommit(true);
                return false;
            }

            int currentQuantity = rs.getInt("quantity");
            int portfolioId = rs.getInt("portfolio_id");

            if (currentQuantity < quantity) {
                System.err.println("Insufficient shares to sell");
                conn.rollback();
                conn.setAutoCommit(true);
                return false;
            }

            rs.close();
            pstmt.close();

            // Record transaction
            String transactionQuery = """
                INSERT INTO transactions (user_id, stock_id, transaction_type, quantity, price_per_share, total_amount, transaction_date)
                VALUES (?, ?, 'SELL', ?, ?, ?, ?)
            """;
            
            double totalAmount = quantity * pricePerShare;
            pstmt = conn.prepareStatement(transactionQuery);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stockId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, pricePerShare);
            pstmt.setDouble(5, totalAmount);
            pstmt.setString(6, LocalDateTime.now().toString());
            pstmt.executeUpdate();
            pstmt.close();

            // Update portfolio
            int newQuantity = currentQuantity - quantity;
            if (newQuantity == 0) {
                // Remove from portfolio
                portfolioService.removeFromPortfolio(portfolioId);
            } else {
                // Update quantity
                portfolioService.updatePortfolioQuantity(portfolioId, newQuantity);
            }

            // Commit transaction
            conn.commit();
            conn.setAutoCommit(true);
            
            System.out.println("Sell transaction successful");
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
            System.err.println("Error processing sell transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all transactions for a user
     */
    public ObservableList<Transaction> getUserTransactions(int userId) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        
        try {
            String query = """
                SELECT t.*, s.symbol, s.company_name
                FROM transactions t
                JOIN stocks s ON t.stock_id = s.stock_id
                WHERE t.user_id = ?
                ORDER BY t.transaction_date DESC
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getInt("user_id"),
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getString("transaction_type"),
                    rs.getInt("quantity"),
                    rs.getDouble("price_per_share"),
                    rs.getDouble("total_amount"),
                    LocalDateTime.parse(rs.getString("transaction_date"))
                );
                transactions.add(transaction);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Get recent transactions (last N)
     */
    public ObservableList<Transaction> getRecentTransactions(int userId, int limit) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        
        try {
            String query = """
                SELECT t.*, s.symbol, s.company_name
                FROM transactions t
                JOIN stocks s ON t.stock_id = s.stock_id
                WHERE t.user_id = ?
                ORDER BY t.transaction_date DESC
                LIMIT ?
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getInt("user_id"),
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getString("transaction_type"),
                    rs.getInt("quantity"),
                    rs.getDouble("price_per_share"),
                    rs.getDouble("total_amount"),
                    LocalDateTime.parse(rs.getString("transaction_date"))
                );
                transactions.add(transaction);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching recent transactions: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }
}
