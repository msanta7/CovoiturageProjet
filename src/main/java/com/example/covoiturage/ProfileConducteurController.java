package com.example.covoiturage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ProfileConducteurController implements Initializable {

    @FXML private Text name;
    @FXML private Text email;
    @FXML private Text num;
    @FXML private Text ville;

    @FXML private Text vehicleModel;
    @FXML private Text vehicleDetails;
    @FXML private Text vehiclePlate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserProfile();
        loadVehicleData(); // Charger les données du véhicule
    }

    private void loadUserProfile() {
        try (Connection connection = Database.connectDB()) {
            int userId = UserSession.getUserId();

            String query = "SELECT nom_complet, email, telephone, ville FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                name.setText(resultSet.getString("nom_complet"));
                email.setText(resultSet.getString("email"));
                num.setText(resultSet.getString("telephone"));

                String userVille = resultSet.getString("ville");
                if (userVille != null && !userVille.isEmpty()) {
                    ville.setText(userVille);
                } else {
                    ville.setText("Non spécifiée");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le profil: " + e.getMessage());
        }
    }

    private void loadVehicleData() {
        try (Connection connection = Database.connectDB()) {
            int userId = UserSession.getUserId();

            String query = "SELECT marque, modele, annee, plaque_immatriculation, nombre_places FROM vehicles WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String marque = resultSet.getString("marque");
                String modele = resultSet.getString("modele");
                String plaque = resultSet.getString("plaque_immatriculation");
                int places = resultSet.getInt("nombre_places");
                int annee = resultSet.getInt("annee");

                // Mettre à jour les Text avec les données du véhicule
                if (vehicleModel != null) {
                    vehicleModel.setText(marque + " " + modele);
                }
                if (vehicleDetails != null) {
                    vehicleDetails.setText(places + " places • " + annee + " • Climatisation");
                }
                if (vehiclePlate != null) {
                    vehiclePlate.setText("Plaque: " + (plaque != null ? plaque : "Non définie"));
                }
            } else {
                // Aucun véhicule trouvé
                if (vehicleModel != null) vehicleModel.setText("Aucun véhicule");
                if (vehicleDetails != null) vehicleDetails.setText("Ajoutez votre véhicule");
                if (vehiclePlate != null) vehiclePlate.setText("Plaque: Non définie");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des données du véhicule: " + e.getMessage());

            // Valeurs par défaut en cas d'erreur
            if (vehicleModel != null) vehicleModel.setText("Erreur de chargement");
            if (vehicleDetails != null) vehicleDetails.setText("Véhicule non disponible");
            if (vehiclePlate != null) vehiclePlate.setText("Plaque: Erreur");
        }
    }

    @FXML
    private void handleModifierProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/edit-profile.fxml"));
            Parent root = loader.load();

            EditProfileController editController = loader.getController();

            Scene scene = new Scene(root);
            Stage popupStage = new Stage();
            popupStage.setScene(scene);
            popupStage.setTitle("Modifier le profil et la voiture");
            popupStage.setResizable(false);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            editController.setStage(popupStage);

            popupStage.showAndWait();

            // Recharger les données après la fermeture de la popup
            loadUserProfile();
            loadVehicleData();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur: " + e.getMessage());
        }
    }

    @FXML
    private void handleDashboardButton() {
        // Votre logique de navigation vers le dashboard
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/covoiturage/DashboardConducteur.fxml"));
            Stage stage = (Stage) name.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    private void handleSettingButton() {
        // Votre logique de navigation vers les paramètres
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/covoiturage/settings.fxml"));
            Stage stage = (Stage) name.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button mesTrajets;

    @FXML
    private void handleTrajetsButton(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/mesTrajets.fxml"));
            Parent root = loader.load();


            Scene currentScene = mesTrajets.getScene();


            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page de profil");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}