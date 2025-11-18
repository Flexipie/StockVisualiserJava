package com.example.stockvisualiser.controller;

import com.example.stockvisualiser.model.User;
import com.example.stockvisualiser.service.AuthenticationService;
import com.example.stockvisualiser.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.io.IOException;

/**
 * LoginController - Handles login and registration functionality
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private TextField regEmailField;
    @FXML private TextField regFullNameField;
    @FXML private ComboBox<String> regRoleComboBox;
    @FXML private Label errorLabel;
    @FXML private Label regErrorLabel;
    @FXML private TabPane tabPane;

    private final AuthenticationService authService;

    public LoginController() {
        this.authService = new AuthenticationService();
    }

    @FXML
    public void initialize() {
        // Setup role combobox
        if (regRoleComboBox != null) {
            regRoleComboBox.getItems().addAll("TRADER", "ADMIN");
            regRoleComboBox.setValue("TRADER");
        }

        // Add Enter key listener to password field
        if (passwordField != null) {
            passwordField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    handleLogin();
                }
            });
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        User user = authService.login(username, password);
        
        if (user != null) {
            SceneManager.setCurrentUser(user);
            try {
                // Load dashboard based on user role
                SceneManager.loadSceneWithController("dashboard.fxml", 
                    "Stock Visualiser - Dashboard", 1200, 800);
            } catch (IOException e) {
                showError("Error loading dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Invalid username or password");
            passwordField.clear();
        }
    }

    @FXML
    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String password = regPasswordField.getText();
        String confirmPassword = regConfirmPasswordField.getText();
        String email = regEmailField.getText().trim();
        String fullName = regFullNameField.getText().trim();
        String roleStr = regRoleComboBox.getValue();

        // Validation
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || fullName.isEmpty()) {
            showRegError("Please fill in all fields");
            return;
        }

        if (username.length() < 3) {
            showRegError("Username must be at least 3 characters");
            return;
        }

        if (password.length() < 6) {
            showRegError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showRegError("Passwords do not match");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showRegError("Please enter a valid email address");
            return;
        }

        User.UserRole role = User.UserRole.valueOf(roleStr);
        
        boolean success = authService.register(username, password, email, fullName, role);
        
        if (success) {
            showRegSuccess("Registration successful! Please login.");
            clearRegistrationFields();
            // Switch to login tab
            tabPane.getSelectionModel().select(0);
        } else {
            showRegError("Registration failed. Username or email may already exist.");
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: #d32f2f;");
        }
    }

    private void showRegError(String message) {
        if (regErrorLabel != null) {
            regErrorLabel.setText(message);
            regErrorLabel.setStyle("-fx-text-fill: #d32f2f;");
        }
    }

    private void showRegSuccess(String message) {
        if (regErrorLabel != null) {
            regErrorLabel.setText(message);
            regErrorLabel.setStyle("-fx-text-fill: #388e3c;");
        }
    }

    private void clearRegistrationFields() {
        regUsernameField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
        regEmailField.clear();
        regFullNameField.clear();
        regRoleComboBox.setValue("TRADER");
    }
}
