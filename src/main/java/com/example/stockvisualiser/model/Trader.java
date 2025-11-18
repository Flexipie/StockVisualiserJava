package com.example.stockvisualiser.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Trader class extending User
 * Demonstrates inheritance - Trader has all User properties plus trading-specific features
 */
public class Trader extends User {
    private List<Portfolio> portfolios;
    private double totalInvestment;
    private double currentPortfolioValue;

    /**
     * Constructor for Trader
     */
    public Trader(int userId, String username, String email, String fullName, LocalDateTime createdAt) {
        super(userId, username, email, fullName, UserRole.TRADER, createdAt);
        this.portfolios = new ArrayList<>();
        this.totalInvestment = 0.0;
        this.currentPortfolioValue = 0.0;
    }

    /**
     * Default constructor
     */
    public Trader() {
        super();
        setRole(UserRole.TRADER);
        this.portfolios = new ArrayList<>();
        this.totalInvestment = 0.0;
        this.currentPortfolioValue = 0.0;
    }

    /**
     * Polymorphic method implementation
     */
    @Override
    public String getDisplayRole() {
        return "Trader/Investor";
    }

    /**
     * Traders cannot manage users
     */
    @Override
    public boolean canManageUsers() {
        return false;
    }

    /**
     * Traders can only view their own portfolio
     */
    @Override
    public boolean canViewAllPortfolios() {
        return false;
    }

    /**
     * Calculate profit/loss percentage
     */
    public double getProfitLossPercentage() {
        if (totalInvestment == 0) return 0.0;
        return ((currentPortfolioValue - totalInvestment) / totalInvestment) * 100;
    }

    /**
     * Calculate total profit/loss amount
     */
    public double getProfitLossAmount() {
        return currentPortfolioValue - totalInvestment;
    }

    // Getters and Setters
    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
    }

    public double getTotalInvestment() {
        return totalInvestment;
    }

    public void setTotalInvestment(double totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    public double getCurrentPortfolioValue() {
        return currentPortfolioValue;
    }

    public void setCurrentPortfolioValue(double currentPortfolioValue) {
        this.currentPortfolioValue = currentPortfolioValue;
    }
}
