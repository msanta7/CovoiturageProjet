package com.example.covoiturage;

import Classes.Trajets;
import Classes.passenger;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfilePassengerController {

    @FXML
    private Text PassengerName;

    @FXML
    private Text PassengerEmail;

    @FXML
    private Text PassengerNum;

    @FXML
    private Text TrajetsEff;

    @FXML
    private Text EconomieReal;

    @FXML
    private Text MemberSince;

    @FXML
    private Text trajetsEFFe;

    @FXML
    private AnchorPane trajetsContainer2; // Changé de VBox à AnchorPane

    @FXML
    private Button dashboardButton;

    @FXML
    private Button settingButton;

    @FXML
    private Button messages;

    @FXML
    private Button modifier;

    @FXML
    private Text ville;

    private final List<Trajets> allTrajets = new ArrayList<>();

    public void initialize() {
        System.out.println("ProfilePassengerController initialized");

        // Vérifier que tous les champs FXML sont correctement liés
        checkFXMLFields();

        // Initialiser des trajets fictifs pour Oran -> Alger
        initializeSampleTrips();

        // Données utilisateur temporaires
        passenger user = new passenger("medjahed", "ayoub", "0799858032", "ayoubmedjahed1@gmail.com", "passwordwsayyi", 2024, 28);

        // Afficher les trajets avec animation
        showAnimatedResults(allTrajets);

        // Mettre à jour les informations utilisateur avec vérifications de null
        safeSetText(PassengerName, user.getLastname() + " " + user.getFirstname());
        safeSetText(PassengerEmail, user.getEmail());
        safeSetText(PassengerNum, user.getPhonenumber());
        safeSetText(TrajetsEff, String.valueOf(user.trajetsAmount));
        safeSetText(MemberSince, String.valueOf(user.getMemberSince()));
        safeSetText(EconomieReal, "2181 DA");
        safeSetText(trajetsEFFe, String.valueOf(user.trajetsAmount));

        // Charger les données réelles depuis la base
        loadUserProfile();
    }

    // Méthode pour initialiser des trajets fictifs Oran -> Alger
    private void initializeSampleTrips() {
        // Vider la liste existante
        allTrajets.clear();

        // Ajouter 3 trajets fictifs pour Oran -> Alger avec différentes dates
        allTrajets.add(new Trajets("Oran", "Alger", LocalDate.now(), "Ahmed", 800, 3));
        allTrajets.add(new Trajets("Oran", "Alger", LocalDate.now().plusDays(1), "Karim", 700, 2));
        allTrajets.add(new Trajets("Oran", "Alger", LocalDate.now().plusDays(2), "Samir", 900, 4));

        System.out.println("Initialisé " + allTrajets.size() + " trajets fictifs Oran -> Alger");
    }

    // Méthode pour simuler une recherche Oran -> Alger
    public void simulateSearchOranToAlger() {
        System.out.println("Recherche simulée: Oran -> Alger");

        // Vider les trajets existants
        allTrajets.clear();

        // Ajouter des trajets spécifiques pour Oran -> Alger avec différentes dates
        allTrajets.add(new Trajets("Oran", "Alger", LocalDate.now(), "Nadia", 750, 3));
        allTrajets.add(new Trajets("Oran", "Alger", LocalDate.now().plusDays(1), "Yassine", 800, 1));
        allTrajets.add(new Trajets("Oran", "Alger", LocalDate.now().plusDays(2), "Fatima", 650, 5));

        // Afficher les résultats
        showAnimatedResults(allTrajets);

        System.out.println("Affichage de " + allTrajets.size() + " trajets Oran -> Alger");
    }

    // Méthode utilitaire pour vérifier les champs FXML
    private void checkFXMLFields() {
        System.out.println("Vérification des champs FXML:");
        System.out.println("PassengerName: " + (PassengerName != null ? "OK" : "NULL"));
        System.out.println("PassengerEmail: " + (PassengerEmail != null ? "OK" : "NULL"));
        System.out.println("PassengerNum: " + (PassengerNum != null ? "OK" : "NULL"));
        System.out.println("TrajetsEff: " + (TrajetsEff != null ? "OK" : "NULL"));
        System.out.println("EconomieReal: " + (EconomieReal != null ? "OK" : "NULL"));
        System.out.println("MemberSince: " + (MemberSince != null ? "OK" : "NULL"));
        System.out.println("trajetsEFFe: " + (trajetsEFFe != null ? "OK" : "NULL"));
        System.out.println("trajetsContainer2: " + (trajetsContainer2 != null ? "OK" : "NULL"));
    }

    // Méthode utilitaire pour éviter les NullPointerException
    private void safeSetText(Text textField, String value) {
        if (textField != null) {
            textField.setText(value);
        } else {
            System.err.println("Champ Text null: " + textField);
        }
    }

    private void showAnimatedResults(List<Trajets> trajets) {
        if (trajetsContainer2 == null) {
            System.err.println("trajetsContainer2 est null - impossible d'afficher les résultats");
            return;
        }

        if (trajets.isEmpty()) {
            trajetsContainer2.setVisible(false); // cacher si aucun résultat
            System.out.println("Aucun trajet à afficher");
            return;
        }

        // Vider le conteneur existant
        trajetsContainer2.getChildren().clear();

        // Afficher le conteneur
        trajetsContainer2.setVisible(true);

        System.out.println("Affichage de " + trajets.size() + " trajets:");

        // Position verticale de départ pour les cartes
        double startY = 0;
        double cardHeight = 80; // Hauteur estimée de chaque carte
        double spacing = 10; // Espacement entre les cartes

        for (int i = 0; i < trajets.size(); i++) {
            Trajets t = trajets.get(i);
            AnchorPane card = createTrajetCard(t);
            card.setOpacity(0); // commencer invisible

            // Positionner la carte avec des ancres
            AnchorPane.setTopAnchor(card, startY);
            AnchorPane.setLeftAnchor(card, 0.0);
            AnchorPane.setRightAnchor(card, 0.0);

            trajetsContainer2.getChildren().add(card);

            // Animation fade-in avec délai progressif
            FadeTransition ft = new FadeTransition(Duration.millis(400), card);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(i * 100)); // Délai progressif pour chaque carte
            ft.play();

            // Incrémenter la position Y pour la prochaine carte
            startY += cardHeight + spacing;

            System.out.println(" - " + t.getDepart() + " -> " + t.getDestination() + " | " +
                    t.getDate() + " | " + t.getPrice() + " DA");
        }
    }

    // CHANGEMENT: Retourne maintenant un AnchorPane au lieu d'un VBox
    private AnchorPane createTrajetCard(Trajets t) {
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; "
                + "-fx-background-radius: 10; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        HBox h1 = new HBox(5);

        Label routeLabel = new Label(t.getDepart() + " → " + t.getDestination());
        routeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label dateLabel = new Label("• " + t.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        h1.getChildren().addAll(routeLabel, dateLabel);

        Label price = new Label(String.format("%d DA", t.getPrice()));
        price.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Bouton "Réserver" pour chaque trajet
        Button reserverBtn = new Button("Réserver");
        reserverBtn.setStyle("-fx-background-color: #4baf50; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 12px;");

        reserverBtn.setOnAction(e -> {
            showAlert("Réservation", "Réservation pour " + t.getDepart() + " -> " +
                    t.getDestination() + " le " + t.getDate() + " à " + t.getPrice() + " DA");
        });

        // Positionner les éléments dans l'AnchorPane
        AnchorPane.setTopAnchor(h1, 10.0);
        AnchorPane.setLeftAnchor(h1, 10.0);

        AnchorPane.setTopAnchor(price, 10.0);
        AnchorPane.setRightAnchor(price, 10.0);

        AnchorPane.setBottomAnchor(reserverBtn, 10.0);
        AnchorPane.setRightAnchor(reserverBtn, 10.0);

        card.getChildren().addAll(h1, price, reserverBtn);

        return card;
    }

    // Méthode pour tester rapidement depuis d'autres contrôleurs
    public void loadOranToAlgerTrips() {
        simulateSearchOranToAlger();
    }

    // ... Le reste de votre code reste inchangé ...
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

    @FXML
    private void handleModifierProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/covoiturage/edit-profile.fxml"));
            Parent root = loader.load();

            EditProfileController editController = loader.getController();
            if (editController != null) {
                editController.setStage(new Stage());
            }

            Scene scene = new Scene(root);
            Stage popupStage = new Stage();
            popupStage.setScene(scene);
            popupStage.setTitle("Modifier le profil");
            popupStage.setResizable(false);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.showAndWait();

            // Recharger les données après la fermeture de la popup
            loadUserProfile();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur: " + e.getMessage());
        }
    }

    private void loadUserProfile() {
        try (Connection connection = Database.connectDB()) {
            int userId = UserSession.getUserId();

            String query = "SELECT nom_complet, email, telephone, ville, date_inscription FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nomComplet = resultSet.getString("nom_complet");
                String userEmail = resultSet.getString("email");
                String telephone = resultSet.getString("telephone");
                String userVille = resultSet.getString("ville");
                java.sql.Date dateInscription = resultSet.getDate("date_inscription");

                // Mettre à jour l'interface avec vérifications de null
                safeSetText(PassengerName, nomComplet);
                safeSetText(PassengerEmail, userEmail);
                safeSetText(PassengerNum, telephone);

                if (ville != null) {
                    ville.setText(userVille != null && !userVille.isEmpty() ? userVille : "Non spécifiée");
                }

                if (MemberSince != null && dateInscription != null) {
                    MemberSince.setText(String.valueOf(dateInscription.getYear()));
                }

                // Charger les statistiques
                loadUserStats(userId);

            } else {
                showAlert("Erreur", "Profil utilisateur non trouvé.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le profil: " + e.getMessage());
        }
    }

    private void loadUserStats(int userId) {
        try (Connection connection = Database.connectDB()) {
            // Compter le nombre de trajets effectués
            String trajetsQuery = "SELECT COUNT(*) as total_trajets FROM reservations WHERE passager_id = ? AND statut = 'confirme'";
            PreparedStatement trajetsStatement = connection.prepareStatement(trajetsQuery);
            trajetsStatement.setInt(1, userId);
            ResultSet trajetsResult = trajetsStatement.executeQuery();

            if (trajetsResult.next()) {
                int totalTrajets = trajetsResult.getInt("total_trajets");
                safeSetText(TrajetsEff, String.valueOf(totalTrajets));
                safeSetText(trajetsEFFe, String.valueOf(totalTrajets));
            }

            // Calculer l'économie
            String economieQuery = "SELECT SUM(prix_total) as total_depense FROM reservations WHERE passager_id = ? AND statut = 'confirme'";
            PreparedStatement economieStatement = connection.prepareStatement(economieQuery);
            economieStatement.setInt(1, userId);
            ResultSet economieResult = economieStatement.executeQuery();

            if (economieResult.next()) {
                double totalDepense = economieResult.getDouble("total_depense");
                double economie = totalDepense * 0.6; // 60% d'économie estimée
                safeSetText(EconomieReal, String.format("%.0f DA", economie));
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            // Valeurs par défaut
            safeSetText(TrajetsEff, "0");
            safeSetText(trajetsEFFe, "0");
            safeSetText(EconomieReal, "0 DA");
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