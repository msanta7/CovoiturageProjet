package com.example.covoiturage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardConducteurController implements Initializable {

    @FXML
    private Button profileButton;
    @FXML
    private Button settingButton;
    @FXML
    private Text userName;

    // Champs du formulaire de publication
    @FXML
    private TextField departField;
    @FXML
    private TextField destinationField;
    @FXML
    private TextField nbrPlaceField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField dateField;
    @FXML
    private Button publierButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DashboardConducteurController initialized");

        // Vérifier que les champs sont bien liés
        checkFXMLFields();

        loadUserName();
        setupPublishButton();
    }

    private void checkFXMLFields() {
        System.out.println("Vérification des champs FXML Dashboard:");
        System.out.println("departField: " + (departField != null ? "OK" : "NULL"));
        System.out.println("destinationField: " + (destinationField != null ? "OK" : "NULL"));
        System.out.println("nbrPlaceField: " + (nbrPlaceField != null ? "OK" : "NULL"));
        System.out.println("prixField: " + (prixField != null ? "OK" : "NULL"));
        System.out.println("dateField: " + (dateField != null ? "OK" : "NULL"));
        System.out.println("publierButton: " + (publierButton != null ? "OK" : "NULL"));
    }

    private void loadUserName() {
        String name = UserSession.getUserName();
        if (name != null && !name.isEmpty()) {
            userName.setText(name);
        } else {
            userName.setText("Utilisateur");
        }
    }

    private void setupPublishButton() {
        if (publierButton != null) {
            publierButton.setOnAction(this::handlePublishTrip);
        } else {
            System.err.println("publierButton est null!");
        }
    }

    @FXML
    private void handlePublishTrip(ActionEvent event) {
        System.out.println("Tentative de publication du trajet...");

        // Vérifier que tous les champs sont remplis
        if (!validateForm()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        // Si tout est valide, publier le trajet
        publishTrip();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Vérifier que les champs ne sont pas null avant de les utiliser
        if (departField == null || departField.getText() == null || departField.getText().trim().isEmpty()) {
            System.err.println("Champ départ vide ou null");
            if (departField != null) highlightField(departField);
            isValid = false;
        } else {
            if (departField != null) removeHighlight(departField);
        }

        if (destinationField == null || destinationField.getText() == null || destinationField.getText().trim().isEmpty()) {
            System.err.println("Champ destination vide ou null");
            if (destinationField != null) highlightField(destinationField);
            isValid = false;
        } else {
            if (destinationField != null) removeHighlight(destinationField);
        }

        if (nbrPlaceField == null || nbrPlaceField.getText() == null || nbrPlaceField.getText().trim().isEmpty() ||
                !isValidNumber(nbrPlaceField.getText())) {
            System.err.println("Champ nombre de places invalide");
            if (nbrPlaceField != null) highlightField(nbrPlaceField);
            isValid = false;
        } else {
            if (nbrPlaceField != null) removeHighlight(nbrPlaceField);
        }

        if (prixField == null || prixField.getText() == null || prixField.getText().trim().isEmpty() ||
                !isValidPrice(prixField.getText())) {
            System.err.println("Champ prix invalide");
            if (prixField != null) highlightField(prixField);
            isValid = false;
        } else {
            if (prixField != null) removeHighlight(prixField);
        }

        if (dateField == null || dateField.getText() == null || dateField.getText().trim().isEmpty()) {
            System.err.println("Champ date vide ou null");
            if (dateField != null) highlightField(dateField);
            isValid = false;
        } else {
            if (dateField != null) removeHighlight(dateField);
        }

        return isValid;
    }

    private boolean isValidNumber(String numberText) {
        try {
            int number = Integer.parseInt(numberText);
            return number > 0 && number <= 8; // Maximum 8 places
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidPrice(String priceText) {
        try {
            // Supprimer "DA" si présent
            String cleanPrice = priceText.replace("DA", "").trim();
            double price = Double.parseDouble(cleanPrice);
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void highlightField(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 10;");
    }

    private void removeHighlight(TextField field) {
        field.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
    }

    private void publishTrip() {
        try {
            // Récupérer les données du formulaire
            String depart = departField.getText().trim();
            String destination = destinationField.getText().trim();
            int nbrPlaces = Integer.parseInt(nbrPlaceField.getText().trim());
            double prix = extractPrice(prixField.getText().trim());
            String dateHeure = dateField.getText().trim();

            // Ici, vous ajouteriez le trajet à votre base de données
            // Pour l'instant, on simule juste la publication

            System.out.println("=== TRAJET PUBLIÉ AVEC SUCCÈS ===");
            System.out.println("Départ: " + depart);
            System.out.println("Destination: " + destination);
            System.out.println("Places disponibles: " + nbrPlaces);
            System.out.println("Prix: " + prix + " DA");
            System.out.println("Date et heure: " + dateHeure);
            System.out.println("=================================");

            // Afficher le message de succès
            showSuccessAlert();

            // Réinitialiser le formulaire
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la publication: " + e.getMessage());
        }
    }

    private double extractPrice(String priceText) {
        // Supprimer "DA" et espaces, puis convertir en double
        String cleanPrice = priceText.replace("DA", "").trim();
        return Double.parseDouble(cleanPrice);
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("✅ Trajet publié avec succès !");
        alert.showAndWait();
    }

    private void clearForm() {
        if (departField != null) departField.clear();
        if (destinationField != null) destinationField.clear();
        if (nbrPlaceField != null) nbrPlaceField.clear();
        if (prixField != null) prixField.clear();
        if (dateField != null) dateField.clear();

        // Retirer les highlights
        if (departField != null) removeHighlight(departField);
        if (destinationField != null) removeHighlight(destinationField);
        if (nbrPlaceField != null) removeHighlight(nbrPlaceField);
        if (prixField != null) removeHighlight(prixField);
        if (dateField != null) removeHighlight(dateField);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
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

    @FXML
    private void handleSettingButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/settings.fxml"));
            Parent root = loader.load();

            Scene currentScene = settingButton.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page de profil");
        }
    }

    @FXML
    private Button mesTrajets;

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
}