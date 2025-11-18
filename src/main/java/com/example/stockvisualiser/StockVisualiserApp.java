package com.example.stockvisualiser;

import com.example.stockvisualiser.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Application class for Stock Visualiser
 * Entry point for the JavaFX application
 */
public class StockVisualiserApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // Initialize database
        DatabaseManager.getInstance();
        
        // Load login screen
        showLoginScreen();
        
        primaryStage.setTitle("Stock Visualiser - Portfolio Management System");
        primaryStage.setOnCloseRequest(event -> {
            // Close database connection on application exit
            DatabaseManager.getInstance().closeConnection();
        });
        
        primaryStage.show();
    }

    /**
     * Show login screen
     */
    public static void showLoginScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StockVisualiserApp.class.getResource("view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setScene(scene);
    }

    /**
     * Get primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
