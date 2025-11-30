package com.example.covoiturage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class SignInController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenom;

    @FXML
    private TextField numField;

    @FXML
    private TextField email;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox accept;

    @FXML
    private CheckBox conducteurRole;

    @FXML
    private Button create;

    @FXML
    public void initialize() {
        // Add event handler for create button
        create.setOnAction(event -> handleCreateAccount());
    }

    private void handleCreateAccount() {
        // Validate required fields
        if (!validateFields()) {
            return;
        }

        // Check if terms are accepted
        if (!accept.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez accepter les conditions d'utilisation");
            return;
        }

        // Check if email already exists
        String emailText = email.getText().trim();
        boolean emailExists = emailExists(emailText);
        System.out.println("Checking email: " + emailText + " - Exists: " + emailExists);

        if (emailExists) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Cet email est déjà utilisé");
            return;
        }

        // Create new user
        try {
            // Determine user type
            String userType = conducteurRole.isSelected() ? "conducteur" : "passager";

            // Hash password
            String hashedPassword = hashPassword(passwordField.getText());

            // Save to database
            boolean success = createUser(
                    nomField.getText().trim(),
                    prenom.getText().trim(),
                    numField.getText().trim(),
                    emailText,
                    hashedPassword,
                    userType
            );

            if (success) {
                // Get the created user info to set session
                int userId = getUserIdByEmail(emailText);
                String userTypeFromDB = getUserTypeByEmail(emailText);

                if (userId != -1) {
                    // Set user session
                    String fullName = nomField.getText() + " " + prenom.getText();
                    UserSession.setCurrentUser(
                            userId,
                            fullName,
                            emailText,
                            userTypeFromDB
                    );

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Compte créé avec succès!");

                    // Redirect to appropriate dashboard based on user type
                    redirectToDashboard(userTypeFromDB);

                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création de la session");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du compte");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // Check if all required fields are filled
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom est obligatoire");
            nomField.requestFocus();
            return false;
        }

        if (prenom.getText() == null || prenom.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prénom est obligatoire");
            prenom.requestFocus();
            return false;
        }

        if (numField.getText() == null || numField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone est obligatoire");
            numField.requestFocus();
            return false;
        }

        if (email.getText() == null || email.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'email est obligatoire");
            email.requestFocus();
            return false;
        }

        if (passwordField.getText() == null || passwordField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe est obligatoire");
            passwordField.requestFocus();
            return false;
        }

        // Validate email format
        if (!isValidEmail(email.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un email valide");
            email.requestFocus();
            return false;
        }

        // Password strength validation
        String password = passwordField.getText();
        if (password.length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe doit contenir au moins 8 caractères");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // Basic email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    // Database methods directly in the controller
    private boolean createUser(String nom, String prenom, String telephone, String email, String password, String userType) {
        String sql = "INSERT INTO users (nom_complet, email, telephone, password_hash, type_utilisateur, date_inscription, notification_push, notification_sms) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String nomComplet = nom + " " + prenom;

            pstmt.setString(1, nomComplet);
            pstmt.setString(2, email);
            pstmt.setString(3, telephone);
            pstmt.setString(4, password);
            pstmt.setString(5, userType);
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setBoolean(7, true);
            pstmt.setBoolean(8, true);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE email = ?";

        try (Connection conn = Database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Email count in database: " + count);
                return count > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = Database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                System.out.println("Found user ID: " + userId);
                return userId;
            }

        } catch (SQLException e) {
            System.out.println("Error getting user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    private String getUserTypeByEmail(String email) {
        String sql = "SELECT type_utilisateur FROM users WHERE email = ?";

        try (Connection conn = Database.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("type_utilisateur");
                System.out.println("Found user type: " + userType);
                return userType;
            }

        } catch (SQLException e) {
            System.out.println("Error getting user type: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("User type not found for email: " + email);
        return null;
    }

    // Simple password hashing method
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private void redirectToDashboard(String userType) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) create.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Compte créé avec succès! Vous pouvez maintenant vous connecter.");

        } catch (IOException e) {
            e.printStackTrace();
            // Fallback: if login.fxml doesn't exist, show success message
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Compte créé avec succès! Veuillez vous connecter.");
        }
    }
    private void clearForm() {
        nomField.clear();
        prenom.clear();
        numField.clear();
        email.clear();
        passwordField.clear();
        accept.setSelected(false);
        conducteurRole.setSelected(false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}