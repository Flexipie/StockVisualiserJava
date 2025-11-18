package com.example.stockvisualiser.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for all users in the system
 * Demonstrates inheritance and encapsulation principles
 */
public abstract class User {
    private int userId;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    /**
     * Enum for user roles
     */
    public enum UserRole {
        ADMIN, TRADER
    }

    /**
     * Constructor for User
     */
    public User(int userId, String username, String email, String fullName, UserRole role, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
    }

    /**
     * Default constructor
     */
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // Abstract method to be implemented by subclasses (Polymorphism)
    public abstract String getDisplayRole();

    // Abstract method for role-specific permissions
    public abstract boolean canManageUsers();
    
    public abstract boolean canViewAllPortfolios();

    // Getters and Setters (Encapsulation)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
}
