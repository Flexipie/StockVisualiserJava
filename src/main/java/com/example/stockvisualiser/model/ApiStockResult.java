package com.example.stockvisualiser.model;

import javafx.beans.property.*;

/**
 * Model for stock search results from API
 */
public class ApiStockResult {
    private final StringProperty symbol;
    private final StringProperty name;
    private final StringProperty type;
    private final StringProperty region;
    
    public ApiStockResult(String symbol, String name, String type, String region) {
        this.symbol = new SimpleStringProperty(symbol);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.region = new SimpleStringProperty(region);
    }
    
    // Symbol
    public String getSymbol() { return symbol.get(); }
    public void setSymbol(String value) { symbol.set(value); }
    public StringProperty symbolProperty() { return symbol; }
    
    // Name
    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }
    
    // Type
    public String getType() { return type.get(); }
    public void setType(String value) { type.set(value); }
    public StringProperty typeProperty() { return type; }
    
    // Region
    public String getRegion() { return region.get(); }
    public void setRegion(String value) { region.set(value); }
    public StringProperty regionProperty() { return region; }
}
