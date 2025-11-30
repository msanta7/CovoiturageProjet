package com.example.covoiturage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;

public class MesTrajetsController {

    @FXML
    private Button dashboardButton;

    @FXML
    private Button settingButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button messages;

    @FXML
    private void handleMessagesButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/PassengerDashboard.fxml"));
            Parent root = loader.load();

            Scene currentScene = messages.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement du tableau de bord: " + e.getMessage());
        }
    }

    @FXML
    private void handleDashboardButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/PassengerDashboard.fxml"));
            Parent root = loader.load();

            Scene currentScene = dashboardButton.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement du tableau de bord: " + e.getMessage());
        }
    }

    @FXML
    private void handleSettingButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/settings.fxml"));
            Parent root = loader.load();

            Scene currentScene = settingButton.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des paramètres: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleProfileButton(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/ProfileConducteur.fxml"));
            Parent root = loader.load();


            Scene currentScene = profileButton.getScene();


            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page de profil");
        }
    }
}
