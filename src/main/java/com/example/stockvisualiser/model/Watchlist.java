package com.example.stockvisualiser.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Watchlist model class for stocks user wants to monitor
 */
public class Watchlist {
    private final IntegerProperty watchlistId;
    private final IntegerProperty userId;
    private final IntegerProperty stockId;
    private final StringProperty symbol;
    private final StringProperty companyName;
    private final StringProperty sector;
    private final DoubleProperty currentPrice;
    private LocalDate addedDate;

    /**
     * Constructor with all parameters
     */
    public Watchlist(int watchlistId, int userId, int stockId, String symbol, String companyName,
                     String sector, double currentPrice, LocalDate addedDate) {
        this.watchlistId = new SimpleIntegerProperty(watchlistId);
        this.userId = new SimpleIntegerProperty(userId);
        this.stockId = new SimpleIntegerProperty(stockId);
        this.symbol = new SimpleStringProperty(symbol);
        this.companyName = new SimpleStringProperty(companyName);
        this.sector = new SimpleStringProperty(sector);
        this.currentPrice = new SimpleDoubleProperty(currentPrice);
        this.addedDate = addedDate;
    }

    /**
     * Default constructor
     */
    public Watchlist() {
        this.watchlistId = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.stockId = new SimpleIntegerProperty();
        this.symbol = new SimpleStringProperty();
        this.companyName = new SimpleStringProperty();
        this.sector = new SimpleStringProperty();
        this.currentPrice = new SimpleDoubleProperty();
        this.addedDate = LocalDate.now();
    }

    // Property getters for JavaFX binding
    public IntegerProperty watchlistIdProperty() {
        return watchlistId;
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

    public StringProperty sectorProperty() {
        return sector;
    }

    public DoubleProperty currentPriceProperty() {
        return currentPrice;
    }

    // Standard getters and setters
    public int getWatchlistId() {
        return watchlistId.get();
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId.set(watchlistId);
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

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }
}
