package com.example.covoiturage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        System.out.println("Login controller initialized");
    }


    @FXML
    private void handlePassengerLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection connection = Database.connectDB()) {
            String query = "SELECT id, nom_complet, email, telephone, password_hash, photo_profil, type_utilisateur " +
                    "FROM users WHERE email = ? AND type_utilisateur = 'passager'";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password_hash");

                if (verifyPassword(password, storedPassword)) {
                    UserSession.setCurrentUser(
                            resultSet.getInt("id"),
                            resultSet.getString("nom_complet"),
                            resultSet.getString("email"),
                            resultSet.getString("type_utilisateur")
                    );

                    redirectToPassengerDashboard();

                } else {
                    showAlert("Erreur", "Mot de passe incorrect.");
                }
            } else {
                showAlert("Erreur", "Aucun compte passager trouvé avec cet email.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la connexion: " + e.getMessage());
        }
    }

    @FXML
    private void handleDriverLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection connection = Database.connectDB()) {
            String query = "SELECT u.id, u.nom_complet, u.email, u.telephone, u.password_hash, " +
                    "u.photo_profil, u.type_utilisateur, v.id as vehicle_id " +
                    "FROM users u " +
                    "LEFT JOIN vehicles v ON u.id = v.user_id " +
                    "WHERE u.email = ? AND u.type_utilisateur = 'conducteur'";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password_hash");

                if (verifyPassword(password, storedPassword)) {
                    // Connexion réussie
                    UserSession.setCurrentUser(
                            resultSet.getInt("id"),
                            resultSet.getString("nom_complet"),
                            resultSet.getString("email"),
                            resultSet.getString("type_utilisateur")
                    );

                    redirectToDriverDashboard();

                } else {
                    showAlert("Erreur", "Mot de passe incorrect.");
                }
            } else {
                showAlert("Erreur", "Aucun compte conducteur trouvé avec cet email.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la connexion: " + e.getMessage());
        }
    }

    // Méthode pour vérifier le mot de passe (à adapter selon votre méthode de hash)
    private boolean verifyPassword(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);

    }

    private void redirectToPassengerDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/covoiturage/PassengerDashboard.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectToDriverDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/covoiturage/DashboardConducteur.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignup() {
        // Logique pour rediriger vers la page d'inscription
        System.out.println("Redirection vers la page d'inscription");
        // Ici vous changerez la scène pour aller vers l'inscription
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}