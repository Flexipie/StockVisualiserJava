package com.example.stockvisualiser.service;

import com.example.stockvisualiser.database.DatabaseManager;
import com.example.stockvisualiser.model.Portfolio;
import com.example.stockvisualiser.model.Stock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

/**
 * PortfolioService - Handles portfolio management operations (CRUD)
 */
public class PortfolioService {
    private final DatabaseManager dbManager;
    private final StockService stockService;

    public PortfolioService() {
        this.dbManager = DatabaseManager.getInstance();
        this.stockService = new StockService();
    }

    /**
     * Get all portfolio holdings for a user
     */
    public ObservableList<Portfolio> getUserPortfolio(int userId) {
        ObservableList<Portfolio> portfolios = FXCollections.observableArrayList();
        
        try {
            String query = """
                SELECT p.*, s.symbol, s.company_name, s.current_price
                FROM portfolio p
                JOIN stocks s ON p.stock_id = s.stock_id
                WHERE p.user_id = ?
                ORDER BY p.purchase_date DESC
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Portfolio portfolio = new Portfolio(
                    rs.getInt("portfolio_id"),
                    rs.getInt("user_id"),
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("purchase_price"),
                    rs.getDouble("current_price"),
                    LocalDate.parse(rs.getString("purchase_date"))
                );
                portfolios.add(portfolio);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching portfolio: " + e.getMessage());
            e.printStackTrace();
        }

        return portfolios;
    }

    /**
     * Add stock to user's portfolio
     */
    public boolean addToPortfolio(int userId, int stockId, int quantity, double purchasePrice, LocalDate purchaseDate) {
        try {
            String query = """
                INSERT INTO portfolio (user_id, stock_id, quantity, purchase_price, purchase_date)
                VALUES (?, ?, ?, ?, ?)
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stockId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, purchasePrice);
            pstmt.setString(5, purchaseDate.toString());

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding to portfolio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update portfolio holding quantity
     */
    public boolean updatePortfolioQuantity(int portfolioId, int newQuantity) {
        try {
            String query = "UPDATE portfolio SET quantity = ? WHERE portfolio_id = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, portfolioId);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating portfolio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove stock from portfolio
     */
    public boolean removeFromPortfolio(int portfolioId) {
        try {
            String query = "DELETE FROM portfolio WHERE portfolio_id = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, portfolioId);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from portfolio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calculate total portfolio value for a user
     */
    public double getTotalPortfolioValue(int userId) {
        double totalValue = 0.0;
        ObservableList<Portfolio> portfolios = getUserPortfolio(userId);
        
        for (Portfolio portfolio : portfolios) {
            totalValue += portfolio.getCurrentValue();
        }
        
        return totalValue;
    }

    /**
     * Calculate total investment for a user
     */
    public double getTotalInvestment(int userId) {
        double totalInvestment = 0.0;
        ObservableList<Portfolio> portfolios = getUserPortfolio(userId);
        
        for (Portfolio portfolio : portfolios) {
            totalInvestment += portfolio.getTotalInvestment();
        }
        
        return totalInvestment;
    }

    /**
     * Calculate total profit/loss for a user
     */
    public double getTotalProfitLoss(int userId) {
        return getTotalPortfolioValue(userId) - getTotalInvestment(userId);
    }

    /**
     * Get portfolio statistics for a user
     */
    public PortfolioStats getPortfolioStats(int userId) {
        double totalValue = getTotalPortfolioValue(userId);
        double totalInvestment = getTotalInvestment(userId);
        double profitLoss = totalValue - totalInvestment;
        double profitLossPercentage = totalInvestment > 0 ? (profitLoss / totalInvestment) * 100 : 0;
        
        return new PortfolioStats(totalValue, totalInvestment, profitLoss, profitLossPercentage);
    }

    /**
     * Inner class for portfolio statistics
     */
    public static class PortfolioStats {
        private final double totalValue;
        private final double totalInvestment;
        private final double profitLoss;
        private final double profitLossPercentage;

        public PortfolioStats(double totalValue, double totalInvestment, double profitLoss, double profitLossPercentage) {
            this.totalValue = totalValue;
            this.totalInvestment = totalInvestment;
            this.profitLoss = profitLoss;
            this.profitLossPercentage = profitLossPercentage;
        }

        public double getTotalValue() { return totalValue; }
        public double getTotalInvestment() { return totalInvestment; }
        public double getProfitLoss() { return profitLoss; }
        public double getProfitLossPercentage() { return profitLossPercentage; }
    }
}
