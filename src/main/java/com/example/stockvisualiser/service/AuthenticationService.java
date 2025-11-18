package com.example.stockvisualiser.service;

import com.example.stockvisualiser.database.DatabaseManager;
import com.example.stockvisualiser.model.Admin;
import com.example.stockvisualiser.model.Trader;
import com.example.stockvisualiser.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * AuthenticationService - Handles user authentication and registration
 * Demonstrates encapsulation and separation of concerns
 */
public class AuthenticationService {
    private final DatabaseManager dbManager;
    private User currentUser;

    public AuthenticationService() {
        this.dbManager = DatabaseManager.getInstance();
        this.currentUser = null;
    }

    /**
     * Authenticate a user with username and password
     * @param username User's username
     * @param password User's password
     * @return User object if authentication successful, null otherwise
     */
    public User login(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                
                // Verify password using BCrypt
                if (BCrypt.checkpw(password, storedHash)) {
                    String role = rs.getString("role");
                    User user;

                    // Create appropriate user object based on role (Polymorphism)
                    if (role.equals("ADMIN")) {
                        user = new Admin(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            LocalDateTime.parse(rs.getString("created_at"))
                        );
                    } else {
                        user = new Trader(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            LocalDateTime.parse(rs.getString("created_at"))
                        );
                    }

                    // Update last login
                    updateLastLogin(user.getUserId());
                    user.setLastLogin(LocalDateTime.now());
                    
                    currentUser = user;
                    System.out.println("Login successful: " + user.getDisplayRole());
                    return user;
                }
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Register a new user
     * @param username Unique username
     * @param password User's password
     * @param email User's email
     * @param fullName User's full name
     * @param role User role (ADMIN or TRADER)
     * @return true if registration successful
     */
    public boolean register(String username, String password, String email, String fullName, User.UserRole role) {
        try {
            // Check if username already exists
            if (usernameExists(username)) {
                System.err.println("Username already exists!");
                return false;
            }

            // Check if email already exists
            if (emailExists(email)) {
                System.err.println("Email already exists!");
                return false;
            }

            // Hash the password
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

            // Insert new user
            String query = """
                INSERT INTO users (username, password_hash, email, full_name, role, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, email);
            pstmt.setString(4, fullName);
            pstmt.setString(5, role.toString());
            pstmt.setString(6, LocalDateTime.now().toString());
            
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Registration successful for: " + username);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if username exists in database
     */
    private boolean usernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt(1) > 0;
        rs.close();
        pstmt.close();
        return exists;
    }

    /**
     * Check if email exists in database
     */
    private boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
        pstmt.setString(1, email);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt(1) > 0;
        rs.close();
        pstmt.close();
        return exists;
    }

    /**
     * Update user's last login timestamp
     */
    private void updateLastLogin(int userId) throws SQLException {
        String query = "UPDATE users SET last_login = ? WHERE user_id = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
        pstmt.setString(1, LocalDateTime.now().toString());
        pstmt.setInt(2, userId);
        pstmt.executeUpdate();
        pstmt.close();
    }

    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
        System.out.println("User logged out successfully.");
    }

    /**
     * Get currently logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
