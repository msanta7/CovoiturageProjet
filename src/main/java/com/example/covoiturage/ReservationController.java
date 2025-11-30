package com.example.covoiturage;

import Classes.Trajets;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {

    // Trip Details Section
    @FXML private Text departVille;
    @FXML private Text arriveeVille;
    @FXML private Text date;
    @FXML private Text heure;
    @FXML private Text duree;

    // Driver Section
    @FXML private Text conducteurName;

    // Passenger Information Section
    @FXML private TextField nomPass;
    @FXML private TextField prenomPass;
    @FXML private TextField emailPass;
    @FXML private TextField telPass;
    @FXML private TextField nbrPlace;

    // Summary Section
    @FXML private Text prix;
    @FXML private Text nbrPlaceFin;
    @FXML private Text fraisService;
    @FXML private Text totalePrix;

    // Button
    @FXML private Button confirmer;

    private Trajets selectedTrajet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ReservationController initialized");

        // Set default values
        if (nbrPlace != null) {
            nbrPlace.setText("1");
            nbrPlace.textProperty().addListener((observable, oldValue, newValue) -> {
                updateSummary();
            });
        }

        // Add event handler for confirm button
        if (confirmer != null) {
            confirmer.setOnAction(event -> handleConfirmerReservation());
        }
    }

    public void setSelectedTrajet(Trajets trajet) {
        this.selectedTrajet = trajet;
        if (selectedTrajet != null) {
            updateUI();
        }
    }

    private void updateUI() {
        try {
            if (selectedTrajet != null) {
                // Update trip details
                if (departVille != null) departVille.setText(selectedTrajet.getDepart());
                if (arriveeVille != null) arriveeVille.setText(selectedTrajet.getDestination());
                if (date != null) date.setText(selectedTrajet.getDate().toString());

                // Set default values for other fields
                if (heure != null) heure.setText("14:30"); // Default time
                if (duree != null) duree.setText("4h 30min"); // Default duration
                if (conducteurName != null) conducteurName.setText(selectedTrajet.getDriverName());
                if (prix != null) prix.setText(selectedTrajet.getPrice() + " DA");

                // Update summary
                updateSummary();

                // Auto-fill passenger info if user is logged in
                autoFillPassengerInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour de l'interface: " + e.getMessage());
        }
    }

    private void autoFillPassengerInfo() {
        // Auto-fill with current user session data if available
        if (UserSession.getUserName() != null) {
            String[] names = UserSession.getUserName().split(" ", 2);
            if (names.length > 0 && nomPass != null) nomPass.setText(names[0]);
            if (names.length > 1 && prenomPass != null) prenomPass.setText(names[1]);
            if (UserSession.getUserEmail() != null && emailPass != null) emailPass.setText(UserSession.getUserEmail());
        }
    }

    private void updateSummary() {
        try {
            if (selectedTrajet != null) {
                int places = 1;
                if (nbrPlace != null && !nbrPlace.getText().isEmpty()) {
                    try {
                        places = Integer.parseInt(nbrPlace.getText());
                        // Ensure places don't exceed available seats
                        if (places > selectedTrajet.getPlaces()) {
                            places = selectedTrajet.getPlaces();
                            nbrPlace.setText(String.valueOf(places));
                        }
                        if (places < 1) {
                            places = 1;
                            nbrPlace.setText("1");
                        }
                    } catch (NumberFormatException e) {
                        places = 1;
                        nbrPlace.setText("1");
                    }
                }

                int pricePerSeat = selectedTrajet.getPrice();
                int totalPrice = pricePerSeat * places;

                // Update summary section
                if (nbrPlaceFin != null) nbrPlaceFin.setText("x " + places);
                if (totalePrix != null) totalePrix.setText(totalPrice + " DA");
                if (fraisService != null) fraisService.setText("0 DA"); // No service fees
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirmerReservation() {
        try {
            // Validate passenger information
            if (!validatePassengerInfo()) {
                return;
            }

            // Validate seat selection
            int placesReserved = getSelectedPlaces();
            if (placesReserved < 1 || placesReserved > selectedTrajet.getPlaces()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de places invalide");
                return;
            }

            // Calculate total price
            int totalPrice = selectedTrajet.getPrice() * placesReserved;

            // Show confirmation
            showAlert(Alert.AlertType.INFORMATION, "Réservation confirmée",
                    "Votre réservation pour " + selectedTrajet.getDepart() + " → " + selectedTrajet.getDestination() +
                            " a été confirmée!\n" +
                            "Places réservées: " + placesReserved + "\n" +
                            "Prix total: " + totalPrice + " DA\n\n" +
                            "Conducteur: " + selectedTrajet.getDriverName() + "\n" +
                            "Date: " + selectedTrajet.getDate());

            // Save reservation to database (you'll need to implement this)
            saveReservationToDatabase(placesReserved, totalPrice);

            // Close the reservation window
            confirmer.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la confirmation: " + e.getMessage());
        }
    }

    private boolean validatePassengerInfo() {
        if (nomPass.getText() == null || nomPass.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir votre nom");
            nomPass.requestFocus();
            return false;
        }

        if (prenomPass.getText() == null || prenomPass.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir votre prénom");
            prenomPass.requestFocus();
            return false;
        }

        if (emailPass.getText() == null || emailPass.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir votre email");
            emailPass.requestFocus();
            return false;
        }

        if (telPass.getText() == null || telPass.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir votre numéro de téléphone");
            telPass.requestFocus();
            return false;
        }

        return true;
    }

    private int getSelectedPlaces() {
        try {
            if (nbrPlace != null && !nbrPlace.getText().isEmpty()) {
                return Integer.parseInt(nbrPlace.getText());
            }
        } catch (NumberFormatException e) {
            // Ignore, return default
        }
        return 1;
    }

    private void saveReservationToDatabase(int placesReserved, int totalPrice) {
        // TODO: Implement database saving logic
        // You'll need to create a reservations table and save:
        // - user_id (from UserSession)
        // - trajet_id (you might need to add this to Trajets class)
        // - places_reserved
        // - total_price
        // - reservation_date
        // - status (confirmed)

        System.out.println("Saving reservation to database:");
        System.out.println("Trajet: " + selectedTrajet.getDepart() + " → " + selectedTrajet.getDestination());
        System.out.println("Places: " + placesReserved);
        System.out.println("Total: " + totalPrice + " DA");
        System.out.println("Passenger: " + nomPass.getText() + " " + prenomPass.getText());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}