package com.example.stockvisualiser.model;

import java.time.LocalDateTime;

/**
 * Admin class extending User
 * Demonstrates inheritance - Admin has all User properties plus additional admin privileges
 */
public class Admin extends User {

    /**
     * Constructor for Admin
     */
    public Admin(int userId, String username, String email, String fullName, LocalDateTime createdAt) {
        super(userId, username, email, fullName, UserRole.ADMIN, createdAt);
    }

    /**
     * Default constructor
     */
    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    /**
     * Polymorphic method implementation
     */
    @Override
    public String getDisplayRole() {
        return "System Administrator";
    }

    /**
     * Admins can manage all users
     */
    @Override
    public boolean canManageUsers() {
        return true;
    }

    /**
     * Admins can view all portfolios
     */
    @Override
    public boolean canViewAllPortfolios() {
        return true;
    }

    /**
     * Admin-specific method to deactivate a user
     */
    public void deactivateUser(int userId) {
        System.out.println("Admin " + getUsername() + " deactivated user ID: " + userId);
    }

    /**
     * Admin-specific method to view system analytics
     */
    public void viewSystemAnalytics() {
        System.out.println("Admin " + getUsername() + " is viewing system analytics");
    }
}
