package com.example.stockvisualiser.controller;

import com.example.stockvisualiser.StockVisualiserApp;
import com.example.stockvisualiser.model.*;
import com.example.stockvisualiser.service.*;
import com.example.stockvisualiser.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * DashboardController - Main dashboard with analytics, portfolio, and transaction management
 * Includes all required features: charts, CRUD operations, search/filter
 */
public class DashboardController {
    // Services
    private final PortfolioService portfolioService;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final WatchlistService watchlistService;
    private final AuthenticationService authService;
    private final StockDataService stockDataService;
    
    // Current user
    private User currentUser;

    // FXML Components - Dashboard Tab
    @FXML private Label welcomeLabel;
    @FXML private Label portfolioValueLabel;
    @FXML private Label investmentLabel;
    @FXML private Label profitLossLabel;
    @FXML private Label profitLossPercentLabel;
    @FXML private PieChart portfolioAllocationChart;
    @FXML private LineChart<String, Number> portfolioPerformanceChart;
    @FXML private BarChart<String, Number> stockComparisonChart;
    @FXML private TableView<Transaction> recentTransactionsTable;
    @FXML private TableColumn<Transaction, String> recentSymbolCol;
    @FXML private TableColumn<Transaction, String> recentTypeCol;
    @FXML private TableColumn<Transaction, Integer> recentQuantityCol;
    @FXML private TableColumn<Transaction, Double> recentPriceCol;
    @FXML private TableColumn<Transaction, Double> recentTotalCol;

    // FXML Components - Portfolio Tab
    @FXML private TableView<Portfolio> portfolioTable;
    @FXML private TableColumn<Portfolio, String> portSymbolCol;
    @FXML private TableColumn<Portfolio, String> portCompanyCol;
    @FXML private TableColumn<Portfolio, Integer> portQuantityCol;
    @FXML private TableColumn<Portfolio, Double> portPurchasePriceCol;
    @FXML private TableColumn<Portfolio, Double> portCurrentPriceCol;
    @FXML private TableColumn<Portfolio, String> portProfitLossCol;

    // FXML Components - Stocks Tab (with real-time search)
    @FXML private TextField stockSearchField;
    @FXML private TableView<Stock> stocksTable;
    @FXML private TableColumn<Stock, String> stockSymbolCol;
    @FXML private TableColumn<Stock, String> stockCompanyCol;
    @FXML private TableColumn<Stock, String> stockSectorCol;
    @FXML private TableColumn<Stock, Double> stockPriceCol;
    @FXML private TextField buyQuantityField;
    @FXML private Label selectedStockLabel;
    @FXML private LineChart<String, Number> stockPriceChart;
    @FXML private javafx.scene.text.Text chartTitleText;

    // FXML Components - Transactions Tab (with search)
    @FXML private TextField transactionSearchField;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> transSymbolCol;
    @FXML private TableColumn<Transaction, String> transCompanyCol;
    @FXML private TableColumn<Transaction, String> transTypeCol;
    @FXML private TableColumn<Transaction, Integer> transQuantityCol;
    @FXML private TableColumn<Transaction, Double> transPriceCol;
    @FXML private TableColumn<Transaction, Double> transTotalCol;
    @FXML private TableColumn<Transaction, String> transDateCol;

    // FXML Components - Watchlist Tab
    @FXML private TableView<Watchlist> watchlistTable;
    @FXML private TableColumn<Watchlist, String> watchSymbolCol;
    @FXML private TableColumn<Watchlist, String> watchCompanyCol;
    @FXML private TableColumn<Watchlist, String> watchSectorCol;
    @FXML private TableColumn<Watchlist, Double> watchPriceCol;

    // FXML Components - Admin Panel (if admin)
    @FXML private VBox adminPanel;
    @FXML private TextField newStockSymbol;
    @FXML private TextField newStockCompany;
    @FXML private TextField newStockSector;
    @FXML private TextField newStockPrice;

    private ObservableList<Stock> allStocks;
    private ObservableList<Transaction> allTransactions;

    public DashboardController() {
        this.portfolioService = new PortfolioService();
        this.stockService = new StockService();
        this.transactionService = new TransactionService();
        this.watchlistService = new WatchlistService();
        this.authService = new AuthenticationService();
        this.stockDataService = new StockDataService();
    }

    @FXML
    public void initialize() {
        currentUser = SceneManager.getCurrentUser();
        
        if (currentUser == null) {
            showError("User session expired. Please login again.");
            handleLogout();
            return;
        }

        setupDashboard();
        setupPortfolioTab();
        setupStocksTab();
        setupTransactionsTab();
        setupWatchlistTab();
        setupAdminPanel();
        
        loadDashboardData();
    }

    private void setupDashboard() {
        welcomeLabel.setText("Welcome, " + currentUser.getFullName() + " (" + currentUser.getDisplayRole() + ")");
        
        // Setup recent transactions table
        if (recentTransactionsTable != null) {
            recentSymbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
            recentTypeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
            recentQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            recentPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerShare"));
            recentTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            
            // Format columns
            recentPriceCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    setText(empty ? null : String.format("$%.2f", price));
                }
            });
            
            recentTotalCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double total, boolean empty) {
                    super.updateItem(total, empty);
                    setText(empty ? null : String.format("$%.2f", total));
                }
            });
        }
    }

    private void setupPortfolioTab() {
        if (portfolioTable != null) {
            portSymbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
            portCompanyCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
            portQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            portPurchasePriceCol.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
            portCurrentPriceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
            
            // Format price columns
            portPurchasePriceCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    setText(empty ? null : String.format("$%.2f", price));
                }
            });
            
            portCurrentPriceCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    setText(empty ? null : String.format("$%.2f", price));
                }
            });
            
            // Profit/Loss column
            portProfitLossCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        Portfolio portfolio = getTableRow().getItem();
                        double profitLoss = portfolio.getProfitLoss();
                        double profitLossPercent = portfolio.getProfitLossPercentage();
                        
                        setText(String.format("$%.2f (%.2f%%)", profitLoss, profitLossPercent));
                        
                        if (profitLoss >= 0) {
                            setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                        }
                    }
                }
            });
        }
    }

    private void setupStocksTab() {
        if (stocksTable != null) {
            stockSymbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
            stockCompanyCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
            stockSectorCol.setCellValueFactory(new PropertyValueFactory<>("sector"));
            stockPriceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
            
            stockPriceCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    setText(empty ? null : String.format("$%.2f", price));
                }
            });
            
            // Real-time search filter
            if (stockSearchField != null) {
                stockSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filterStocks(newValue);
                });
            }
            
            // Row selection - update selected label AND chart
            stocksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedStockLabel.setText("Selected: " + newSelection.getSymbol() + " - " + newSelection.getCompanyName() + 
                                             " | Price: $" + String.format("%.2f", newSelection.getCurrentPrice()));
                    // Update price chart with historical data
                    updateStockPriceChart(newSelection.getSymbol());
                }
            });
        }
    }

    private void setupTransactionsTab() {
        if (transactionsTable != null) {
            transSymbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
            transCompanyCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
            transTypeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
            transQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            transPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerShare"));
            transTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            
            // Format price columns
            transPriceCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    setText(empty ? null : String.format("$%.2f", price));
                }
            });
            
            transTotalCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double total, boolean empty) {
                    super.updateItem(total, empty);
                    setText(empty ? null : String.format("$%.2f", total));
                }
            });
            
            // Date column
            transDateCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                    } else {
                        Transaction trans = getTableRow().getItem();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
                        setText(trans.getTransactionDate().format(formatter));
                    }
                }
            });
            
            // Color code transaction types
            transTypeCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String type, boolean empty) {
                    super.updateItem(type, empty);
                    if (empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(type);
                        if ("BUY".equals(type)) {
                            setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                        }
                    }
                }
            });
            
            // Real-time search filter for transactions
            if (transactionSearchField != null) {
                transactionSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filterTransactions(newValue);
                });
            }
        }
    }

    private void setupWatchlistTab() {
        if (watchlistTable != null) {
            watchSymbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
            watchCompanyCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));
            watchSectorCol.setCellValueFactory(new PropertyValueFactory<>("sector"));
            watchPriceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
            
            watchPriceCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    setText(empty ? null : String.format("$%.2f", price));
                }
            });
        }
    }

    private void setupAdminPanel() {
        // Show/hide admin panel based on user role
        if (adminPanel != null) {
            adminPanel.setVisible(currentUser.canManageUsers());
            adminPanel.setManaged(currentUser.canManageUsers());
        }
    }

    private void loadDashboardData() {
        loadPortfolioStats();
        loadPortfolioTable();
        loadStocksTable();
        loadTransactionsTable();
        loadWatchlistTable();
        loadCharts();
        loadRecentTransactions();
    }

    private void loadPortfolioStats() {
        PortfolioService.PortfolioStats stats = portfolioService.getPortfolioStats(currentUser.getUserId());
        
        portfolioValueLabel.setText(String.format("$%.2f", stats.getTotalValue()));
        investmentLabel.setText(String.format("$%.2f", stats.getTotalInvestment()));
        profitLossLabel.setText(String.format("$%.2f", stats.getProfitLoss()));
        profitLossPercentLabel.setText(String.format("%.2f%%", stats.getProfitLossPercentage()));
        
        // Apply styling
        if (stats.getProfitLoss() >= 0) {
            profitLossLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
            profitLossPercentLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
        } else {
            profitLossLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            profitLossPercentLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        }
    }

    private void loadPortfolioTable() {
        ObservableList<Portfolio> portfolio = portfolioService.getUserPortfolio(currentUser.getUserId());
        portfolioTable.setItems(portfolio);
    }

    private void loadStocksTable() {
        allStocks = stockService.getAllStocks();
        stocksTable.setItems(allStocks);
    }

    private void loadTransactionsTable() {
        allTransactions = transactionService.getUserTransactions(currentUser.getUserId());
        transactionsTable.setItems(allTransactions);
    }

    private void loadWatchlistTable() {
        ObservableList<Watchlist> watchlist = watchlistService.getUserWatchlist(currentUser.getUserId());
        watchlistTable.setItems(watchlist);
    }

    private void loadRecentTransactions() {
        ObservableList<Transaction> recent = transactionService.getRecentTransactions(currentUser.getUserId(), 5);
        recentTransactionsTable.setItems(recent);
    }

    private void loadCharts() {
        loadPortfolioAllocationChart();
        loadStockComparisonChart();
    }

    private void loadPortfolioAllocationChart() {
        ObservableList<Portfolio> portfolio = portfolioService.getUserPortfolio(currentUser.getUserId());
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Group by stock symbol
        for (Portfolio holding : portfolio) {
            pieChartData.add(new PieChart.Data(
                holding.getSymbol(), 
                holding.getCurrentValue()
            ));
        }
        
        portfolioAllocationChart.setData(pieChartData);
        portfolioAllocationChart.setTitle("Portfolio Allocation by Stock");
    }

    @SuppressWarnings("unchecked")
    private void loadStockComparisonChart() {
        ObservableList<Portfolio> portfolio = portfolioService.getUserPortfolio(currentUser.getUserId());
        
        CategoryAxis xAxis = (CategoryAxis) stockComparisonChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) stockComparisonChart.getYAxis();
        
        xAxis.setLabel("Stock Symbol");
        yAxis.setLabel("Value ($)");
        
        XYChart.Series<String, Number> investmentSeries = new XYChart.Series<>();
        investmentSeries.setName("Investment");
        
        XYChart.Series<String, Number> currentValueSeries = new XYChart.Series<>();
        currentValueSeries.setName("Current Value");
        
        for (Portfolio holding : portfolio) {
            investmentSeries.getData().add(new XYChart.Data<>(holding.getSymbol(), holding.getTotalInvestment()));
            currentValueSeries.getData().add(new XYChart.Data<>(holding.getSymbol(), holding.getCurrentValue()));
        }
        
        stockComparisonChart.getData().clear();
        stockComparisonChart.getData().addAll(investmentSeries, currentValueSeries);
    }

    // Real-time search/filter methods
    private void filterStocks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            stocksTable.setItems(allStocks);
        } else {
            ObservableList<Stock> filteredStocks = FXCollections.observableArrayList();
            String lowerCaseFilter = searchTerm.toLowerCase();
            
            for (Stock stock : allStocks) {
                if (stock.getSymbol().toLowerCase().contains(lowerCaseFilter) ||
                    stock.getCompanyName().toLowerCase().contains(lowerCaseFilter) ||
                    stock.getSector().toLowerCase().contains(lowerCaseFilter)) {
                    filteredStocks.add(stock);
                }
            }
            
            stocksTable.setItems(filteredStocks);
        }
    }

    private void filterTransactions(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            transactionsTable.setItems(allTransactions);
        } else {
            ObservableList<Transaction> filteredTransactions = FXCollections.observableArrayList();
            String lowerCaseFilter = searchTerm.toLowerCase();
            
            for (Transaction transaction : allTransactions) {
                if (transaction.getSymbol().toLowerCase().contains(lowerCaseFilter) ||
                    transaction.getCompanyName().toLowerCase().contains(lowerCaseFilter) ||
                    transaction.getTransactionType().toLowerCase().contains(lowerCaseFilter)) {
                    filteredTransactions.add(transaction);
                }
            }
            
            transactionsTable.setItems(filteredTransactions);
        }
    }

    // FXML Action Methods
    @FXML
    private void handleBuyStock() {
        Stock selected = stocksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a stock to buy");
            return;
        }
        
        String quantityStr = buyQuantityField.getText().trim();
        if (quantityStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter quantity");
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Quantity must be greater than 0");
                return;
            }
            
            double totalCost = quantity * selected.getCurrentPrice();
            
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Purchase");
            confirmation.setHeaderText("Buy " + quantity + " shares of " + selected.getSymbol());
            confirmation.setContentText(String.format(
                "Price per share: $%.2f\nTotal cost: $%.2f\n\nConfirm purchase?",
                selected.getCurrentPrice(), totalCost
            ));
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = transactionService.buyStock(
                    currentUser.getUserId(),
                    selected.getStockId(),
                    quantity,
                    selected.getCurrentPrice()
                );
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Stock purchased successfully!");
                    buyQuantityField.clear();
                    loadDashboardData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to purchase stock");
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid number");
        }
    }

    @FXML
    private void handleSellStock() {
        Portfolio selected = portfolioTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a stock from your portfolio to sell");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sell Stock");
        dialog.setHeaderText("Sell " + selected.getSymbol());
        dialog.setContentText("Enter quantity to sell (max: " + selected.getQuantity() + "):");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantityStr -> {
            try {
                int quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0 || quantity > selected.getQuantity()) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Quantity", 
                        "Quantity must be between 1 and " + selected.getQuantity());
                    return;
                }
                
                double totalRevenue = quantity * selected.getCurrentPrice();
                
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Sale");
                confirmation.setHeaderText("Sell " + quantity + " shares of " + selected.getSymbol());
                confirmation.setContentText(String.format(
                    "Price per share: $%.2f\nTotal revenue: $%.2f\n\nConfirm sale?",
                    selected.getCurrentPrice(), totalRevenue
                ));
                
                Optional<ButtonType> confirmResult = confirmation.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    boolean success = transactionService.sellStock(
                        currentUser.getUserId(),
                        selected.getStockId(),
                        quantity,
                        selected.getCurrentPrice()
                    );
                    
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Stock sold successfully!");
                        loadDashboardData();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to sell stock");
                    }
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid number");
            }
        });
    }

    @FXML
    private void handleAddToWatchlist() {
        Stock selected = stocksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a stock to add to watchlist");
            return;
        }
        
        boolean success = watchlistService.addToWatchlist(currentUser.getUserId(), selected.getStockId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Stock added to watchlist!");
            loadWatchlistTable();
        } else {
            showAlert(Alert.AlertType.WARNING, "Already Added", "This stock is already in your watchlist");
        }
    }

    @FXML
    private void handleRemoveFromWatchlist() {
        Watchlist selected = watchlistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a stock to remove");
            return;
        }
        
        boolean success = watchlistService.removeFromWatchlist(selected.getWatchlistId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Stock removed from watchlist!");
            loadWatchlistTable();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove stock from watchlist");
        }
    }

    @FXML
    private void handleAddStock() {
        String symbol = newStockSymbol.getText().trim();
        String company = newStockCompany.getText().trim();
        String sector = newStockSector.getText().trim();
        String priceStr = newStockPrice.getText().trim();
        
        if (symbol.isEmpty() || company.isEmpty() || sector.isEmpty() || priceStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please fill in all fields");
            return;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Price", "Price must be greater than 0");
                return;
            }
            
            boolean success = stockService.addStock(symbol.toUpperCase(), company, sector, price);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Stock added successfully!");
                newStockSymbol.clear();
                newStockCompany.clear();
                newStockSector.clear();
                newStockPrice.clear();
                loadStocksTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add stock");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Price", "Please enter a valid price");
        }
    }

    @FXML
    private void handleRefresh() {
        loadDashboardData();
        showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Dashboard data refreshed successfully!");
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        SceneManager.setCurrentUser(null);
        try {
            StockVisualiserApp.showLoginScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the stock price chart with historical data
     */
    private void updateStockPriceChart(String symbol) {
        if (stockPriceChart == null || chartTitleText == null) {
            return;
        }
        
        try {
            // Update chart title
            chartTitleText.setText(symbol + " - 30 Day Price History");
            
            // Fetch historical price data
            ObservableList<StockDataService.PriceData> historicalData = stockDataService.getHistoricalPrices(symbol);
            
            if (historicalData.isEmpty()) {
                chartTitleText.setText("No data available for " + symbol);
                stockPriceChart.getData().clear();
                return;
            }
            
            // Find min and max prices for better Y-axis scaling
            double minPrice = Double.MAX_VALUE;
            double maxPrice = Double.MIN_VALUE;
            for (StockDataService.PriceData priceData : historicalData) {
                minPrice = Math.min(minPrice, priceData.getPrice());
                maxPrice = Math.max(maxPrice, priceData.getPrice());
            }
            
            // Add 5% padding to min/max for better visualization
            double padding = (maxPrice - minPrice) * 0.05;
            double yAxisMin = minPrice - padding;
            double yAxisMax = maxPrice + padding;
            
            // Set Y-axis range dynamically
            NumberAxis yAxis = (NumberAxis) stockPriceChart.getYAxis();
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(yAxisMin);
            yAxis.setUpperBound(yAxisMax);
            yAxis.setTickUnit((yAxisMax - yAxisMin) / 8); // ~8 tick marks
            
            // Create series for the chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(symbol + " Price");
            
            // Sample every 3rd point to reduce X-axis label crowding (30 days → 10 labels)
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
            for (int i = 0; i < historicalData.size(); i += 3) {
                StockDataService.PriceData priceData = historicalData.get(i);
                String dateStr = priceData.getDate().format(dateFormatter);
                series.getData().add(new XYChart.Data<>(dateStr, priceData.getPrice()));
            }
            
            // Always add the last point to show most recent data
            if (historicalData.size() % 3 != 1) {
                StockDataService.PriceData lastPoint = historicalData.get(historicalData.size() - 1);
                String dateStr = lastPoint.getDate().format(dateFormatter);
                series.getData().add(new XYChart.Data<>(dateStr, lastPoint.getPrice()));
            }
            
            // Update chart
            stockPriceChart.getData().clear();
            stockPriceChart.getData().add(series);
            
            // Apply modern gradient styling to the line
            String priceChange = maxPrice > historicalData.get(0).getPrice() ? "up" : "down";
            String lineColor = priceChange.equals("up") ? "#4caf50" : "#f44336";
            series.getNode().setStyle(
                "-fx-stroke: " + lineColor + ";" +
                "-fx-stroke-width: 3px;" +
                "-fx-effect: dropshadow(gaussian, " + lineColor + "80, 6, 0.5, 0, 2);"
            );
            
            // Add tooltips and style data points
            for (XYChart.Data<String, Number> data : series.getData()) {
                Tooltip tooltip = new Tooltip(
                    "📅 " + data.getXValue() + "\n" +
                    "💰 $" + String.format("%.2f", data.getYValue())
                );
                tooltip.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-background-color: rgba(0, 0, 0, 0.8);" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 6px;" +
                    "-fx-padding: 8px;"
                );
                Tooltip.install(data.getNode(), tooltip);
                
                // Style data points with gradient
                if (data.getNode() != null) {
                    data.getNode().setStyle(
                        "-fx-background-color: " + lineColor + ";" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 4px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 4, 0, 0, 1);"
                    );
                }
            }
            
            // Update chart title with price info
            double priceChangePercent = ((maxPrice - historicalData.get(0).getPrice()) / historicalData.get(0).getPrice()) * 100;
            String arrow = priceChangePercent >= 0 ? "📈" : "📉";
            chartTitleText.setText(String.format("%s %s - 30 Day History (%+.2f%%)", 
                symbol, arrow, priceChangePercent));
            
        } catch (Exception e) {
            System.err.println("Error updating price chart: " + e.getMessage());
            e.printStackTrace();
            chartTitleText.setText("Error loading data for " + symbol);
        }
    }

    /**
     * Refresh the currently displayed stock chart
     */
    @FXML
    private void handleRefreshChart() {
        Stock selectedStock = stocksTable.getSelectionModel().getSelectedItem();
        if (selectedStock != null) {
            updateStockPriceChart(selectedStock.getSymbol());
            showAlert(Alert.AlertType.INFORMATION, "Chart Refreshed", 
                     "Price chart updated for " + selectedStock.getSymbol());
        } else {
            showError("Please select a stock first");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }
}
