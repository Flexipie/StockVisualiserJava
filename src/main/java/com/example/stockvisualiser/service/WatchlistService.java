package com.example.stockvisualiser.service;

import com.example.stockvisualiser.database.DatabaseManager;
import com.example.stockvisualiser.model.Watchlist;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

/**
 * WatchlistService - Manages user's stock watchlist
 */
public class WatchlistService {
    private final DatabaseManager dbManager;

    public WatchlistService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Get user's watchlist
     */
    public ObservableList<Watchlist> getUserWatchlist(int userId) {
        ObservableList<Watchlist> watchlist = FXCollections.observableArrayList();
        
        try {
            String query = """
                SELECT w.*, s.symbol, s.company_name, s.sector, s.current_price
                FROM watchlist w
                JOIN stocks s ON w.stock_id = s.stock_id
                WHERE w.user_id = ?
                ORDER BY w.added_date DESC
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Watchlist item = new Watchlist(
                    rs.getInt("watchlist_id"),
                    rs.getInt("user_id"),
                    rs.getInt("stock_id"),
                    rs.getString("symbol"),
                    rs.getString("company_name"),
                    rs.getString("sector"),
                    rs.getDouble("current_price"),
                    LocalDate.parse(rs.getString("added_date"))
                );
                watchlist.add(item);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching watchlist: " + e.getMessage());
            e.printStackTrace();
        }

        return watchlist;
    }

    /**
     * Add stock to watchlist
     */
    public boolean addToWatchlist(int userId, int stockId) {
        try {
            String query = """
                INSERT INTO watchlist (user_id, stock_id, added_date)
                VALUES (?, ?, ?)
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stockId);
            pstmt.setString(3, LocalDate.now().toString());

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint")) {
                System.err.println("Stock already in watchlist");
            } else {
                System.err.println("Error adding to watchlist: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Remove stock from watchlist
     */
    public boolean removeFromWatchlist(int watchlistId) {
        try {
            String query = "DELETE FROM watchlist WHERE watchlist_id = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, watchlistId);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from watchlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if stock is in user's watchlist
     */
    public boolean isInWatchlist(int userId, int stockId) {
        try {
            String query = "SELECT COUNT(*) FROM watchlist WHERE user_id = ? AND stock_id = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stockId);
            ResultSet rs = pstmt.executeQuery();

            boolean exists = rs.next() && rs.getInt(1) > 0;
            rs.close();
            pstmt.close();

            return exists;
        } catch (SQLException e) {
            System.err.println("Error checking watchlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
