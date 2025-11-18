package com.example.stockvisualiser.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Stock model class representing a stock in the market
 * Uses JavaFX properties for table binding
 */
public class Stock {
    private final IntegerProperty stockId;
    private final StringProperty symbol;
    private final StringProperty companyName;
    private final StringProperty sector;
    private final DoubleProperty currentPrice;
    private LocalDateTime lastUpdated;

    /**
     * Constructor with all parameters
     */
    public Stock(int stockId, String symbol, String companyName, String sector, double currentPrice, LocalDateTime lastUpdated) {
        this.stockId = new SimpleIntegerProperty(stockId);
        this.symbol = new SimpleStringProperty(symbol);
        this.companyName = new SimpleStringProperty(companyName);
        this.sector = new SimpleStringProperty(sector);
        this.currentPrice = new SimpleDoubleProperty(currentPrice);
        this.lastUpdated = lastUpdated;
    }

    /**
     * Default constructor
     */
    public Stock() {
        this.stockId = new SimpleIntegerProperty();
        this.symbol = new SimpleStringProperty();
        this.companyName = new SimpleStringProperty();
        this.sector = new SimpleStringProperty();
        this.currentPrice = new SimpleDoubleProperty();
        this.lastUpdated = LocalDateTime.now();
    }

    // Property getters for JavaFX binding
    public IntegerProperty stockIdProperty() {
        return stockId;
    }

    public StringProperty symbolProperty() {
        return symbol;
    }

    public StringProperty companyNameProperty() {
        return companyName;
    }

    public StringProperty sectorProperty() {
        return sector;
    }

    public DoubleProperty currentPriceProperty() {
        return currentPrice;
    }

    // Standard getters and setters
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

    public String getSector() {
        return sector.get();
    }

    public void setSector(String sector) {
        this.sector.set(sector);
    }

    public double getCurrentPrice() {
        return currentPrice.get();
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice.set(currentPrice);
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return symbol.get() + " - " + companyName.get();
    }
}
