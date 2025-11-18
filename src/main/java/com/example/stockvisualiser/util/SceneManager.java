package com.example.stockvisualiser.util;

import com.example.stockvisualiser.StockVisualiserApp;
import com.example.stockvisualiser.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * SceneManager - Utility class for managing scene transitions
 */
public class SceneManager {
    private static User currentUser;

    /**
     * Load a scene from FXML file
     */
    public static void loadScene(String fxmlPath, String title, int width, int height) throws IOException {
        Stage stage = StockVisualiserApp.getPrimaryStage();
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/com/example/stockvisualiser/view/" + fxmlPath));
        Scene scene = new Scene(loader.load(), width, height);
        stage.setScene(scene);
        stage.setTitle(title);
    }

    /**
     * Load scene with controller access
     */
    public static <T> T loadSceneWithController(String fxmlPath, String title, int width, int height) throws IOException {
        Stage stage = StockVisualiserApp.getPrimaryStage();
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/com/example/stockvisualiser/view/" + fxmlPath));
        Scene scene = new Scene(loader.load(), width, height);
        stage.setScene(scene);
        stage.setTitle(title);
        return loader.getController();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
