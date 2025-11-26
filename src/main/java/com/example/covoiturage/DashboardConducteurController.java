package com.example.covoiturage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserName();
    }

    private void loadUserName() {
        String name = UserSession.getUserName();
        if (name != null && !name.isEmpty()) {
            userName.setText(name);
        } else {
            userName.setText("Utilisateur");
        }
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
}