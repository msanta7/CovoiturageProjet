package com.example.covoiturage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class EditProfileController implements Initializable {

    // Champs utilisateur
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField villeField;

    // Champs véhicule
    @FXML private TextField marqueField;
    @FXML private TextField modeleField;
    @FXML private TextField anneeField;
    @FXML private TextField plaqueField;
    @FXML private TextField placesField;

    private Stage stage;
    private int vehicleId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les données actuelles de l'utilisateur et du véhicule
        loadCurrentUserData();
        loadCurrentVehicleData();
    }

    private void loadCurrentUserData() {
        try (Connection connection = Database.connectDB()) {
            int userId = UserSession.getUserId();

            String query = "SELECT nom_complet, email, telephone, ville FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                nomField.setText(resultSet.getString("nom_complet"));
                emailField.setText(resultSet.getString("email"));
                telephoneField.setText(resultSet.getString("telephone"));

                String ville = resultSet.getString("ville");
                if (ville != null) {
                    villeField.setText(ville);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données utilisateur: " + e.getMessage());
        }
    }

    private void loadCurrentVehicleData() {
        try (Connection connection = Database.connectDB()) {
            int userId = UserSession.getUserId();

            // Récupérer le véhicule de l'utilisateur
            String query = "SELECT id, marque, modele, annee, plaque_immatriculation, nombre_places FROM vehicles WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                vehicleId = resultSet.getInt("id");
                marqueField.setText(resultSet.getString("marque"));
                modeleField.setText(resultSet.getString("modele"));

                int annee = resultSet.getInt("annee");
                if (annee != 0) {
                    anneeField.setText(String.valueOf(annee));
                }

                plaqueField.setText(resultSet.getString("plaque_immatriculation"));
                placesField.setText(String.valueOf(resultSet.getInt("nombre_places")));
            } else {
                // Pas de véhicule existant, laisser les champs vides
                vehicleId = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données du véhicule: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        // Récupérer les données utilisateur
        String nom = nomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String ville = villeField.getText();

        // Récupérer les données véhicule
        String marque = marqueField.getText();
        String modele = modeleField.getText();
        String anneeText = anneeField.getText();
        String plaque = plaqueField.getText();
        String placesText = placesField.getText();

        // Validation des champs utilisateur
        if (nom.isEmpty() || email.isEmpty()) {
            showAlert("Erreur", "Le nom et l'email sont obligatoires.");
            return;
        }

        // Validation des champs véhicule
        if (marque.isEmpty() || modele.isEmpty() || plaque.isEmpty() || placesText.isEmpty() || anneeText.isEmpty()) {
            showAlert("Erreur", "Tous les champs du véhicule sont obligatoires.");
            return;
        }

        try {
            int annee = Integer.parseInt(anneeText);
            int places = Integer.parseInt(placesText);

            if (places <= 0) {
                showAlert("Erreur", "Le nombre de places doit être supérieur à 0.");
                return;
            }

            if (annee < 1900 || annee > 2030) {
                showAlert("Erreur", "L'année doit être comprise entre 1900 et 2030.");
                return;
            }

            try (Connection connection = Database.connectDB()) {
                // Mettre à jour l'utilisateur
                String userQuery = "UPDATE users SET nom_complet = ?, email = ?, telephone = ?, ville = ? WHERE id = ?";
                PreparedStatement userStatement = connection.prepareStatement(userQuery);
                userStatement.setString(1, nom);
                userStatement.setString(2, email);
                userStatement.setString(3, telephone);
                userStatement.setString(4, ville);
                userStatement.setInt(5, UserSession.getUserId());

                int userRowsUpdated = userStatement.executeUpdate();

                // Mettre à jour ou créer le véhicule
                String vehicleQuery;
                PreparedStatement vehicleStatement;

                if (vehicleId > 0) {
                    // Mettre à jour le véhicule existant
                    vehicleQuery = "UPDATE vehicles SET marque = ?, modele = ?, annee = ?, plaque_immatriculation = ?, nombre_places = ? WHERE id = ?";
                    vehicleStatement = connection.prepareStatement(vehicleQuery);
                    vehicleStatement.setString(1, marque);
                    vehicleStatement.setString(2, modele);
                    vehicleStatement.setInt(3, annee);
                    vehicleStatement.setString(4, plaque);
                    vehicleStatement.setInt(5, places);
                    vehicleStatement.setInt(6, vehicleId);
                } else {
                    // Créer un nouveau véhicule
                    vehicleQuery = "INSERT INTO vehicles (user_id, marque, modele, annee, plaque_immatriculation, nombre_places) VALUES (?, ?, ?, ?, ?, ?)";
                    vehicleStatement = connection.prepareStatement(vehicleQuery);
                    vehicleStatement.setInt(1, UserSession.getUserId());
                    vehicleStatement.setString(2, marque);
                    vehicleStatement.setString(3, modele);
                    vehicleStatement.setInt(4, annee);
                    vehicleStatement.setString(5, plaque);
                    vehicleStatement.setInt(6, places);
                }

                int vehicleRowsUpdated = vehicleStatement.executeUpdate();

                if (userRowsUpdated > 0 || vehicleRowsUpdated > 0) {
                    showAlert("Succès", "Profil et véhicule mis à jour avec succès!");

                    // Mettre à jour la session si l'email a changé
                    if (!email.equals(UserSession.getUserEmail())) {
                        UserSession.setCurrentUser(
                                UserSession.getUserId(),
                                nom,
                                email,
                                UserSession.getUserType()
                        );
                    }

                    stage.close();
                } else {
                    showAlert("Erreur", "Aucune modification effectuée.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'année et le nombre de places doivent être des nombres valides.");
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}