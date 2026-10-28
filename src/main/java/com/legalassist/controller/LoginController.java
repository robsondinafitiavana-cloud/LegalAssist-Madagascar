package com.legalassist.controller;

import com.legalassist.Main;
import com.legalassist.dao.UserDAO;
import com.legalassist.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * ============================================================
 * CLASSE LoginController
 * ============================================================
 * Contrôleur associé à l'écran login.fxml.
 * Un "contrôleur" (au sens MVC) réagit aux actions de
 * l'utilisateur (clics, saisie...) et fait le lien entre
 * l'interface graphique (FXML) et la logique métier (services/DAO).
 *
 * Les annotations @FXML permettent de relier automatiquement
 * un attribut Java à un composant défini dans le fichier FXML,
 * grâce à leur attribut fx:id.
 * ============================================================
 */
public class LoginController {

    @FXML
    private TextField champLogin;

    @FXML
    private PasswordField champMotDePasse;

    @FXML
    private Label labelErreur;

    private final UserDAO userDAO = new UserDAO();

    /**
     * Méthode appelée automatiquement quand l'utilisateur clique
     * sur le bouton "Se connecter" (voir onAction="#seConnecter" dans le FXML).
     */
    @FXML
    private void seConnecter(ActionEvent event) {
        String login = champLogin.getText();
        String motDePasse = champMotDePasse.getText();

        if (login == null || login.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            labelErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        User user = userDAO.authentifier(login, motDePasse);

        if (user == null) {
            labelErreur.setText("Login ou mot de passe incorrect.");
            return;
        }

        // Connexion réussie : on mémorise l'utilisateur et on ouvre le tableau de bord
        SessionUtilisateur.connecter(user);

        try {
            Main.changerEcran("/fxml/main_layout.fxml");
            Main.getStagePrincipal().setTitle("LegalAssist Madagascar AI - Tableau de bord");
        } catch (IOException e) {
            labelErreur.setText("Erreur lors du chargement de l'application.");
            System.err.println(e.getMessage());
        }
    }
}
