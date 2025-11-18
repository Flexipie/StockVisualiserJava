package com.example.stockvisualiser.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Portfolio model class representing a user's stock holding
 * Uses JavaFX properties for table binding
 */
public class Portfolio {
    private final IntegerProperty portfolioId;
    private final IntegerProperty userId;
    private final IntegerProperty stockId;
    private final StringProperty symbol;
    private final StringProperty companyName;
    private final IntegerProperty quantity;
    private final DoubleProperty purchasePrice;
    private final DoubleProperty currentPrice;
    private LocalDate purchaseDate;

    /**
     * Constructor with all parameters
     */
    public Portfolio(int portfolioId, int userId, int stockId, String symbol, String companyName,
                     int quantity, double purchasePrice, double currentPrice, LocalDate purchaseDate) {
        this.portfolioId = new SimpleIntegerProperty(portfolioId);
        this.userId = new SimpleIntegerProperty(userId);
        this.stockId = new SimpleIntegerProperty(stockId);
        this.symbol = new SimpleStringProperty(symbol);
        this.companyName = new SimpleStringProperty(companyName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.purchasePrice = new SimpleDoubleProperty(purchasePrice);
        this.currentPrice = new SimpleDoubleProperty(currentPrice);
        this.purchaseDate = purchaseDate;
    }

    /**
     * Default constructor
     */
    public Portfolio() {
        this.portfolioId = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.stockId = new SimpleIntegerProperty();
        this.symbol = new SimpleStringProperty();
        this.companyName = new SimpleStringProperty();
        this.quantity = new SimpleIntegerProperty();
        this.purchasePrice = new SimpleDoubleProperty();
        this.currentPrice = new SimpleDoubleProperty();
        this.purchaseDate = LocalDate.now();
    }

    /**
     * Calculate total investment for this holding
     */
    public double getTotalInvestment() {
        return quantity.get() * purchasePrice.get();
    }

    /**
     * Calculate current value of this holding
     */
    public double getCurrentValue() {
        return quantity.get() * currentPrice.get();
    }

    /**
     * Calculate profit/loss amount
     */
    public double getProfitLoss() {
        return getCurrentValue() - getTotalInvestment();
    }

    /**
     * Calculate profit/loss percentage
     */
    public double getProfitLossPercentage() {
        if (getTotalInvestment() == 0) return 0.0;
        return (getProfitLoss() / getTotalInvestment()) * 100;
    }

    // Property getters for JavaFX binding
    public IntegerProperty portfolioIdProperty() {
        return portfolioId;
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public IntegerProperty stockIdProperty() {
        return stockId;
    }

    public StringProperty symbolProperty() {
        return symbol;
    }

    public StringProperty companyNameProperty() {
        return companyName;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public DoubleProperty purchasePriceProperty() {
        return purchasePrice;
    }

    public DoubleProperty currentPriceProperty() {
        return currentPrice;
    }

    // Standard getters and setters
    public int getPortfolioId() {
        return portfolioId.get();
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId.set(portfolioId);
    }

    public int getUserId() {
        return userId.get();
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public int getStockId() {
        return stockId.get();
    }

    public void setStockId(int stockId) {
        this.stockId.set(stockId);
    }

    public String getSymbol() {
        return symbol.get();
    }

    public void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }

    public String getCompanyName() {
        return companyName.get();
    }

    public void setCompanyName(String companyName) {
        this.companyName.set(companyName);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getPurchasePrice() {
        return purchasePrice.get();
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice.set(purchasePrice);
    }

    public double getCurrentPrice() {
        return currentPrice.get();
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice.set(currentPrice);
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
