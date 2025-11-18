package com.example.stockvisualiser.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Transaction model class representing buy/sell transactions
 * Demonstrates encapsulation and data modeling
 */
public class Transaction {
    private final IntegerProperty transactionId;
    private final IntegerProperty userId;
    private final IntegerProperty stockId;
    private final StringProperty symbol;
    private final StringProperty companyName;
    private final StringProperty transactionType;
    private final IntegerProperty quantity;
    private final DoubleProperty pricePerShare;
    private final DoubleProperty totalAmount;
    private LocalDateTime transactionDate;

    /**
     * Transaction type enum
     */
    public enum TransactionType {
        BUY, SELL
    }

    /**
     * Constructor with all parameters
     */
    public Transaction(int transactionId, int userId, int stockId, String symbol, String companyName,
                       String transactionType, int quantity, double pricePerShare, 
                       double totalAmount, LocalDateTime transactionDate) {
        this.transactionId = new SimpleIntegerProperty(transactionId);
        this.userId = new SimpleIntegerProperty(userId);
        this.stockId = new SimpleIntegerProperty(stockId);
        this.symbol = new SimpleStringProperty(symbol);
        this.companyName = new SimpleStringProperty(companyName);
        this.transactionType = new SimpleStringProperty(transactionType);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.pricePerShare = new SimpleDoubleProperty(pricePerShare);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.transactionDate = transactionDate;
    }

    /**
     * Default constructor
     */
    public Transaction() {
        this.transactionId = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.stockId = new SimpleIntegerProperty();
        this.symbol = new SimpleStringProperty();
        this.companyName = new SimpleStringProperty();
        this.transactionType = new SimpleStringProperty();
        this.quantity = new SimpleIntegerProperty();
        this.pricePerShare = new SimpleDoubleProperty();
        this.totalAmount = new SimpleDoubleProperty();
        this.transactionDate = LocalDateTime.now();
    }

    // Property getters for JavaFX binding
    public IntegerProperty transactionIdProperty() {
        return transactionId;
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

    public StringProperty transactionTypeProperty() {
        return transactionType;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public DoubleProperty pricePerShareProperty() {
        return pricePerShare;
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    // Standard getters and setters
    public int getTransactionId() {
        return transactionId.get();
    }

    public void setTransactionId(int transactionId) {
        this.transactionId.set(transactionId);
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

    public String getTransactionType() {
        return transactionType.get();
    }

    public void setTransactionType(String transactionType) {
        this.transactionType.set(transactionType);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getPricePerShare() {
        return pricePerShare.get();
    }

    public void setPricePerShare(double pricePerShare) {
        this.pricePerShare.set(pricePerShare);
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount.set(totalAmount);
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
