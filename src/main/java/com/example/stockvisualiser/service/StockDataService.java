package com.example.stockvisualiser.service;

import com.example.stockvisualiser.model.ApiStockResult;
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
    private static final String API_KEY = "2UPAIY6PD8V75FJF"; // Use "demo" for testing with limited stocks
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
        System.out.println("=== FETCHING DATA FOR: " + symbol + " ===");
        System.out.println("API Key configured: " + (API_KEY != null && !API_KEY.isEmpty() ? "YES" : "NO"));
        System.out.println("API Key: " + (API_KEY.length() > 4 ? API_KEY.substring(0, 4) + "..." : "EMPTY"));
        
        // Try to fetch from API first
        ObservableList<PriceData> apiData = fetchFromAPI(symbol);
        if (apiData != null && !apiData.isEmpty()) {
            System.out.println("✓ Successfully fetched " + apiData.size() + " data points from API");
            return apiData;
        }
        
        System.out.println("⚠ API fetch failed, using simulated data");
        // Fallback to simulated realistic data
        return generateSimulatedData(symbol);
    }
    
    /**
     * Fetch real data from Alpha Vantage API
     */
    private ObservableList<PriceData> fetchFromAPI(String symbol) {
        try {
            // TIME_SERIES_DAILY gives us daily stock prices
            String urlString = String.format(
                "%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s&outputsize=compact",
                BASE_URL, symbol, API_KEY
            );
            
            System.out.println("🌐 API URL: " + urlString.replace(API_KEY, "***KEY***"));
            
            URI uri = new URI(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            System.out.println("📡 HTTP Response Code: " + responseCode);
            
            if (responseCode != 200) {
                System.err.println("❌ API returned non-200 status: " + responseCode);
                return null;
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            String jsonResponse = response.toString();
            System.out.println("📥 Response length: " + jsonResponse.length() + " characters");
            System.out.println("📄 First 200 chars: " + jsonResponse.substring(0, Math.min(200, jsonResponse.length())));
            
            // Check for API error messages
            if (jsonResponse.contains("Error Message") || jsonResponse.contains("Note")) {
                System.err.println("⚠ API Error in response:");
                System.err.println(jsonResponse.substring(0, Math.min(500, jsonResponse.length())));
            }
            
            // Parse JSON response (simple parsing without external library)
            ObservableList<PriceData> data = parseAPIResponse(jsonResponse);
            if (data == null || data.isEmpty()) {
                System.err.println("❌ Parsing failed or returned empty data");
            }
            return data;
            
        } catch (Exception e) {
            System.err.println("❌ API fetch exception for " + symbol + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Simple JSON parsing for Alpha Vantage response
     * Format: {"Time Series (Daily)": {"2025-11-17": {"4. close": "123.45"}}}
     */
    private ObservableList<PriceData> parseAPIResponse(String json) {
        ObservableList<PriceData> data = FXCollections.observableArrayList();
        
        try {
            System.out.println("🔍 Starting JSON parsing...");
            
            // Look for "Time Series (Daily)" section
            int timeSeriesIndex = json.indexOf("\"Time Series (Daily)\"");
            if (timeSeriesIndex == -1) {
                System.err.println("❌ Could not find 'Time Series (Daily)' in response");
                return null;
            }
            
            System.out.println("✓ Found Time Series data at index " + timeSeriesIndex);
            
            // Extract the time series object
            String timeSeriesData = json.substring(timeSeriesIndex);
            
            // Split by date entries (look for "YYYY-MM-DD" pattern)
            String[] parts = timeSeriesData.split("\"\\d{4}-\\d{2}-\\d{2}\"");
            
            System.out.println("📊 Found " + (parts.length - 1) + " date entries");
            
            // Extract all dates first
            java.util.regex.Pattern datePattern = java.util.regex.Pattern.compile("\"(\\d{4}-\\d{2}-\\d{2})\"");
            java.util.regex.Matcher dateMatcher = datePattern.matcher(timeSeriesData);
            
            java.util.List<String> dates = new java.util.ArrayList<>();
            while (dateMatcher.find() && dates.size() < 30) {
                dates.add(dateMatcher.group(1));
            }
            
            System.out.println("📅 Extracted " + dates.size() + " dates");
            
            // For each date, find its closing price
            for (String dateStr : dates) {
                try {
                    // Find the data block for this date
                    int dateIndex = json.indexOf("\"" + dateStr + "\"");
                    if (dateIndex == -1) continue;
                    
                    // Look for "4. close" within the next 500 characters
                    String dataBlock = json.substring(dateIndex, Math.min(dateIndex + 500, json.length()));
                    
                    // Extract closing price using regex
                    java.util.regex.Pattern pricePattern = java.util.regex.Pattern.compile("\"4\\. close\":\\s*\"([0-9.]+)\"");
                    java.util.regex.Matcher priceMatcher = pricePattern.matcher(dataBlock);
                    
                    if (priceMatcher.find()) {
                        String priceStr = priceMatcher.group(1);
                        double price = Double.parseDouble(priceStr);
                        LocalDate date = LocalDate.parse(dateStr);
                        data.add(new PriceData(date, price));
                        
                        if (data.size() <= 3) {
                            System.out.println("  ✓ " + dateStr + " → $" + String.format("%.2f", price));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠ Failed to parse date " + dateStr + ": " + e.getMessage());
                }
            }
            
            System.out.println("✅ Successfully parsed " + data.size() + " price points");
            return data.isEmpty() ? null : data;
            
        } catch (Exception e) {
            System.err.println("❌ JSON parsing exception: " + e.getMessage());
            e.printStackTrace();
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
    
    /**
     * Search for stocks using Alpha Vantage SYMBOL_SEARCH endpoint
     * Allows users to find real stocks to add to the system
     */
    public ObservableList<ApiStockResult> searchStocks(String keywords) {
        ObservableList<ApiStockResult> results = FXCollections.observableArrayList();
        
        try {
            System.out.println("🔍 Searching API for: " + keywords);
            
            String urlString = String.format(
                "%s?function=SYMBOL_SEARCH&keywords=%s&apikey=%s",
                BASE_URL, keywords.replace(" ", "%20"), API_KEY
            );
            
            System.out.println("🌐 Search URL: " + urlString.replace(API_KEY, "***KEY***"));
            
            URI uri = new URI(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            System.out.println("📡 Response: " + responseCode);
            
            if (responseCode != 200) {
                return results;
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            String jsonResponse = response.toString();
            System.out.println("📥 Got " + jsonResponse.length() + " characters");
            
            // Parse search results
            results = parseSearchResults(jsonResponse);
            System.out.println("✅ Found " + results.size() + " matching stocks");
            
        } catch (Exception e) {
            System.err.println("❌ Stock search failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Parse Alpha Vantage SYMBOL_SEARCH response
     * Format: {"bestMatches": [{"1. symbol": "TSLA", "2. name": "Tesla Inc.", ...}]}
     */
    private ObservableList<ApiStockResult> parseSearchResults(String json) {
        ObservableList<ApiStockResult> results = FXCollections.observableArrayList();
        
        try {
            // Look for "bestMatches" array
            int matchesIndex = json.indexOf("\"bestMatches\"");
            if (matchesIndex == -1) {
                return results;
            }
            
            String matchesSection = json.substring(matchesIndex);
            
            // Find each match using regex patterns
            java.util.regex.Pattern symbolPattern = java.util.regex.Pattern.compile("\"1\\. symbol\":\\s*\"([^\"]+)\"");
            java.util.regex.Pattern namePattern = java.util.regex.Pattern.compile("\"2\\. name\":\\s*\"([^\"]+)\"");
            java.util.regex.Pattern typePattern = java.util.regex.Pattern.compile("\"3\\. type\":\\s*\"([^\"]+)\"");
            java.util.regex.Pattern regionPattern = java.util.regex.Pattern.compile("\"4\\. region\":\\s*\"([^\"]+)\"");
            
            // Split by closing braces to get individual results
            String[] parts = matchesSection.split("\\},\\s*\\{");
            
            for (String part : parts) {
                try {
                    java.util.regex.Matcher symbolMatcher = symbolPattern.matcher(part);
                    java.util.regex.Matcher nameMatcher = namePattern.matcher(part);
                    java.util.regex.Matcher typeMatcher = typePattern.matcher(part);
                    java.util.regex.Matcher regionMatcher = regionPattern.matcher(part);
                    
                    if (symbolMatcher.find() && nameMatcher.find()) {
                        String symbol = symbolMatcher.group(1);
                        String name = nameMatcher.group(1);
                        String type = typeMatcher.find() ? typeMatcher.group(1) : "Equity";
                        String region = regionMatcher.find() ? regionMatcher.group(1) : "Unknown";
                        
                        results.add(new ApiStockResult(symbol, name, type, region));
                        
                        if (results.size() <= 3) {
                            System.out.println("  ✓ " + symbol + " - " + name);
                        }
                    }
                } catch (Exception e) {
                    // Skip malformed entries
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Parse error: " + e.getMessage());
        }
        
        return results;
    }
}
