package com.example.covoiturage;

import Classes.Trajets;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerDashboardController {

    @FXML
    private Button profileButton;
    @FXML
    private Button settingButton;
    @FXML
    private Button RechercherButton;
    @FXML
    private ChoiceBox<String> departField;
    @FXML
    private ChoiceBox<String> destinationField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private VBox trajetsContainer;

    private final List<Trajets> allTrajets = new ArrayList<>();

    @FXML
    public void initialize() {
        // Enhanced test data with multiple examples for each route
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Oran → Alger route (3 examples)
        allTrajets.add(new Trajets("Oran", "Alger", today, "Ahmed", 800, 2));
        allTrajets.add(new Trajets("Oran", "Alger", today, "Karim", 750, 4));
        allTrajets.add(new Trajets("Oran", "Alger", tomorrow, "Samir", 700, 3));

        // Alger → Constantine route (3 examples)
        allTrajets.add(new Trajets("Alger", "Constantine", today, "Yassine", 1200, 1));
        allTrajets.add(new Trajets("Alger", "Constantine", today, "Nadia", 1100, 2));
        allTrajets.add(new Trajets("Alger", "Constantine", tomorrow, "Mohamed", 1150, 3));

        // Oran → Constantine route (3 examples)
        allTrajets.add(new Trajets("Oran", "Constantine", today, "Fatima", 1500, 2));
        allTrajets.add(new Trajets("Oran", "Constantine", tomorrow, "Khalid", 1450, 4));
        allTrajets.add(new Trajets("Oran", "Constantine", tomorrow, "Leila", 1400, 1));

        // Alger → Oran route (3 examples)
        allTrajets.add(new Trajets("Alger", "Oran", today, "Rachid", 850, 3));
        allTrajets.add(new Trajets("Alger", "Oran", today, "Sofia", 800, 2));
        allTrajets.add(new Trajets("Alger", "Oran", tomorrow, "Tarek", 780, 5));

        // Fill the ChoiceBoxes
        List<String> departOptions = allTrajets.stream()
                .map(Trajets::getDepart)
                .distinct()
                .sorted()
                .toList();
        departField.getItems().addAll(departOptions);

        List<String> destinationOptions = allTrajets.stream()
                .map(Trajets::getDestination)
                .distinct()
                .sorted()
                .toList();
        destinationField.getItems().addAll(destinationOptions);

        trajetsContainer.setVisible(true);
    }

    @FXML
    private void handleRechercherButton() {
        String depart = departField.getValue();
        String destination = destinationField.getValue();
        LocalDate date = datePicker.getValue();

        if (depart == null || destination == null || date == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs de recherche");
            return;
        }

        List<Trajets> results = allTrajets.stream()
                .filter(t -> t.getDepart().equalsIgnoreCase(depart))
                .filter(t -> t.getDestination().equalsIgnoreCase(destination))
                .filter(t -> t.getDate().isEqual(date))
                .collect(Collectors.toList());

        showAnimatedResults(results);
    }

    private void showAnimatedResults(List<Trajets> trajets) {
        trajetsContainer.getChildren().clear();

        if (trajets.isEmpty()) {
            Label noResults = new Label("Aucun trajet trouvé");
            noResults.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-padding: 20;");
            trajetsContainer.getChildren().add(noResults);
            trajetsContainer.setVisible(true);
            return;
        }

        trajetsContainer.setVisible(true);

        for (Trajets t : trajets) {
            VBox card = createTrajetCard(t);
            card.setOpacity(0);

            trajetsContainer.getChildren().add(card);

            FadeTransition ft = new FadeTransition(Duration.millis(400), card);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    // Updated to make cards clickable
    private VBox createTrajetCard(Trajets t) {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5,0,0,2); " +
                "-fx-cursor: hand;");

        // Add hover effect
        box.setOnMouseEntered(e -> {
            box.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; " +
                    "-fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 6,0,0,3); " +
                    "-fx-cursor: hand;");
        });

        box.setOnMouseExited(e -> {
            box.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; " +
                    "-fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5,0,0,2); " +
                    "-fx-cursor: hand;");
        });

        // Make the entire card clickable
        box.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                openReservationWindow(t);
            }
        });

        // Route and Date
        HBox routeRow = new HBox(10);
        Label routeLabel = new Label(t.getDepart() + " → " + t.getDestination());
        routeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");

        Label dateLabel = new Label("• " + t.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        routeRow.getChildren().addAll(routeLabel, dateLabel);

        // Driver and Rating
        HBox driverRow = new HBox(10);
        Label driverLabel = new Label("Conducteur: " + t.getDriverName());
        driverLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3436;");

        Label ratingLabel = new Label("⭐ " + t.getRating());
        ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12;");
        driverRow.getChildren().addAll(driverLabel, ratingLabel);

        // Price and Seats
        HBox infoRow = new HBox(10);
        Label priceLabel = new Label(t.getPrice() + " DA");
        priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label seatsLabel = new Label(t.getPlaces() + " places libres");
        seatsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        infoRow.getChildren().addAll(priceLabel, seatsLabel);

        // Click instruction
        Label clickInfo = new Label("Cliquez pour réserver");
        clickInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #933cc3; -fx-font-style: italic;");

        box.getChildren().addAll(routeRow, driverRow, infoRow, clickInfo);

        return box;
    }

    private void openReservationWindow(Trajets trajet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/reservation.fxml"));
            Parent root = loader.load();

            // Pass the selected trajet to the reservation controller
            ReservationController reservationController = loader.getController();
            reservationController.setSelectedTrajet(trajet);

            Stage reservationStage = new Stage();
            reservationStage.setTitle("Réservation - " + trajet.getDepart() + " → " + trajet.getDestination());
            reservationStage.setScene(new Scene(root));
            reservationStage.setResizable(false);

            // Make it modal (optional - blocks interaction with main window)
           reservationStage.initModality(Modality.APPLICATION_MODAL);

            reservationStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de réservation");
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

    @FXML
    private void handleProfileButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/ProfilePassenger.fxml"));
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
            System.err.println("Erreur lors du chargement de la page de paramètres");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}