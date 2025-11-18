package com.example.stockvisualiser.service;

import com.example.stockvisualiser.database.DatabaseManager;
import com.example.stockvisualiser.model.Stock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * StockService - Handles all stock-related operations
 */
public class StockService {
    private final DatabaseManager dbManager;

    public StockService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Get all stocks from database
     */
    public ObservableList<Stock> getAllStocks() {
        ObservableList<Stock> stocks = FXCollections.observableArrayList();
        
        try {
            String query = "SELECT * FROM stocks ORDER BY symbol";
            Statement stmt = dbManager.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Stock stock = new Stock(
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getString("sector"),
                    rs.getDouble("current_price"),
                    LocalDateTime.parse(rs.getString("last_updated"))
                );
                stocks.add(stock);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching stocks: " + e.getMessage());
            e.printStackTrace();
        }

        return stocks;
    }

    /**
     * Get stock by ID
     */
    public Stock getStockById(int stockId) {
        try {
            String query = "SELECT * FROM stocks WHERE stock_id = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, stockId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Stock stock = new Stock(
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getString("sector"),
                    rs.getDouble("current_price"),
                    LocalDateTime.parse(rs.getString("last_updated"))
                );
                rs.close();
                pstmt.close();
                return stock;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching stock: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get stock by symbol
     */
    public Stock getStockBySymbol(String symbol) {
        try {
            String query = "SELECT * FROM stocks WHERE symbol = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setString(1, symbol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Stock stock = new Stock(
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getString("sector"),
                    rs.getDouble("current_price"),
                    LocalDateTime.parse(rs.getString("last_updated"))
                );
                rs.close();
                pstmt.close();
                return stock;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching stock: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a new stock to the database
     */
    public boolean addStock(String symbol, String companyName, String sector, double currentPrice) {
        try {
            String query = """
                INSERT INTO stocks (symbol, company_name, sector, current_price, last_updated)
                VALUES (?, ?, ?, ?, ?)
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setString(1, symbol);
            pstmt.setString(2, companyName);
            pstmt.setString(3, sector);
            pstmt.setDouble(4, currentPrice);
            pstmt.setString(5, LocalDateTime.now().toString());

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update stock price
     */
    public boolean updateStockPrice(int stockId, double newPrice) {
        try {
            String query = "UPDATE stocks SET current_price = ?, last_updated = ? WHERE stock_id = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setDouble(1, newPrice);
            pstmt.setString(2, LocalDateTime.now().toString());
            pstmt.setInt(3, stockId);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock price: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a stock
     */
    public boolean deleteStock(int stockId) {
        try {
            String query = "DELETE FROM stocks WHERE stock_id = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, stockId);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Search stocks by symbol or company name
     */
    public ObservableList<Stock> searchStocks(String searchTerm) {
        ObservableList<Stock> stocks = FXCollections.observableArrayList();
        
        try {
            String query = """
                SELECT * FROM stocks 
                WHERE symbol LIKE ? OR company_name LIKE ?
                ORDER BY symbol
            """;
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Stock stock = new Stock(
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getString("sector"),
                    rs.getDouble("current_price"),
                    LocalDateTime.parse(rs.getString("last_updated"))
                );
                stocks.add(stock);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error searching stocks: " + e.getMessage());
            e.printStackTrace();
        }

        return stocks;
    }
}
