package com.example.stockvisualiser.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StockDataService - Fetches live stock data from Alpha Vantage API
 * For demo purposes, also includes simulated data fallback
 */
public class StockDataService {
    
    // Alpha Vantage API key (free tier: 25 requests/day)
    // Get your own key at: https://www.alphavantage.co/support/#api-key
    private static final String API_KEY = "demo"; // Use "demo" for testing with limited stocks
    private static final String BASE_URL = "https://www.alphavantage.co/query";
    
    /**
     * Stock price data point
     */
    public static class PriceData {
        private final LocalDate date;
        private final double price;
        
        public PriceData(LocalDate date, double price) {
            this.date = date;
            this.price = price;
        }
        
        public LocalDate getDate() { return date; }
        public double getPrice() { return price; }
    }
    
    /**
     * Fetch historical stock prices for the last 30 days
     * Falls back to simulated data if API fails or for demo purposes
     */
    public ObservableList<PriceData> getHistoricalPrices(String symbol) {
        // Try to fetch from API first
        ObservableList<PriceData> apiData = fetchFromAPI(symbol);
        if (apiData != null && !apiData.isEmpty()) {
            return apiData;
        }
        
        // Fallback to simulated realistic data
        return generateSimulatedData(symbol);
    }
    
    /**
     * Fetch real data from Alpha Vantage API
     */
    private ObservableList<PriceData> fetchFromAPI(String symbol) {
        try {
            // TIME_SERIES_DAILY_ADJUSTED gives us daily stock prices
            String urlString = String.format(
                "%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s&outputsize=compact",
                BASE_URL, symbol, API_KEY
            );
            
            URI uri = new URI(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return null;
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            // Parse JSON response (simple parsing without external library)
            return parseAPIResponse(response.toString());
            
        } catch (Exception e) {
            System.err.println("API fetch failed for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Simple JSON parsing for Alpha Vantage response
     */
    private ObservableList<PriceData> parseAPIResponse(String json) {
        ObservableList<PriceData> data = FXCollections.observableArrayList();
        
        try {
            // Look for "Time Series (Daily)" section
            int timeSeriesIndex = json.indexOf("\"Time Series (Daily)\"");
            if (timeSeriesIndex == -1) {
                return null; // API limit reached or invalid response
            }
            
            String timeSeriesData = json.substring(timeSeriesIndex);
            
            // Extract dates and closing prices (last 30 days)
            String[] lines = timeSeriesData.split(",");
            int count = 0;
            
            for (String line : lines) {
                if (count >= 30) break;
                
                // Look for date pattern "YYYY-MM-DD"
                if (line.contains("\"") && line.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {
                    String dateStr = line.replaceAll("[^0-9-]", "").trim();
                    if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        // Find the closing price for this date
                        int closingIndex = timeSeriesData.indexOf("\"4. close\"", timeSeriesIndex);
                        if (closingIndex != -1) {
                            String priceStr = timeSeriesData.substring(closingIndex + 11, closingIndex + 30);
                            priceStr = priceStr.split("\"")[1];
                            
                            double price = Double.parseDouble(priceStr);
                            LocalDate date = LocalDate.parse(dateStr);
                            data.add(new PriceData(date, price));
                            count++;
                        }
                    }
                }
            }
            
            return data.isEmpty() ? null : data;
            
        } catch (Exception e) {
            System.err.println("JSON parsing failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate simulated realistic stock price data for demo
     * Creates a 30-day trend with realistic volatility
     */
    private ObservableList<PriceData> generateSimulatedData(String symbol) {
        ObservableList<PriceData> data = FXCollections.observableArrayList();
        List<PriceData> tempList = new ArrayList<>();
        
        // Base price from symbol (different stocks have different price ranges)
        double basePrice = getBasePriceForSymbol(symbol);
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        
        // Generate a trend (upward, downward, or sideways)
        double trendFactor = (Math.random() - 0.5) * 0.003; // -0.15% to +0.15% daily trend
        
        double currentPrice = basePrice;
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Skip weekends
            if (date.getDayOfWeek().getValue() >= 6) {
                continue;
            }
            
            // Apply trend and random volatility
            double dailyChange = (Math.random() - 0.5) * 0.04; // -2% to +2% daily volatility
            currentPrice *= (1 + trendFactor + dailyChange);
            
            // Keep price positive
            currentPrice = Math.max(currentPrice, basePrice * 0.7);
            
            tempList.add(new PriceData(date, currentPrice));
        }
        
        // Reverse to show oldest first
        Collections.reverse(tempList);
        data.addAll(tempList);
        
        return data;
    }
    
    /**
     * Get base price for different stock symbols
     */
    private double getBasePriceForSymbol(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "AAPL" -> 175.50;
            case "GOOGL" -> 140.30;
            case "MSFT" -> 380.75;
            case "AMZN" -> 145.20;
            case "TSLA" -> 235.60;
            case "META" -> 330.40;
            case "NVDA" -> 495.80;
            case "NFLX" -> 445.90;
            case "AMD" -> 115.30;
            case "INTC" -> 42.50;
            default -> 100.0;
        };
    }
    
    /**
     * Get current price (latest from historical data)
     */
    public double getCurrentPrice(String symbol) {
        ObservableList<PriceData> historicalData = getHistoricalPrices(symbol);
        if (historicalData.isEmpty()) {
            return 0.0;
        }
        // Return the most recent price
        return historicalData.get(historicalData.size() - 1).getPrice();
    }
}
