package com.legalassist.controller;

import com.legalassist.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * ============================================================
 * CLASSE MainLayoutController
 * ============================================================
 * Contrôleur du "cadre" principal de l'application (sidebar +
 * zone de contenu), affiché juste après la connexion.
 *
 * PRINCIPE IMPORTANT (différent des anciens écrans) :
 * Avant, chaque clic sur un bouton de navigation remplaçait
 * TOUTE la fenêtre (Main.changerEcran(...)), ce qui rechargeait
 * même la sidebar à chaque fois. Maintenant, la sidebar reste
 * fixe : seul le contenu à droite (le StackPane "zoneContenu")
 * est remplacé, via chargerContenu(...).
 * ============================================================
 */
public class MainLayoutController {

    @FXML private StackPane zoneContenu;

    @FXML private Label labelUtilisateurConnecte;

    @FXML private Button boutonDashboard;
    @FXML private Button boutonDossiers;
    @FXML private Button boutonLois;
    @FXML private Button boutonAudiences;
    @FXML private Button boutonAssistantIA;
    @FXML private Button boutonEntrainementIA;

    /** Appelée automatiquement par JavaFX au chargement de main_layout.fxml. */
    @FXML
    public void initialize() {
        var utilisateur = SessionUtilisateur.getUtilisateurConnecte();
        if (utilisateur != null) {
            labelUtilisateurConnecte.setText(utilisateur.getPrenom() + " " + utilisateur.getNom()
                    + "\n(" + utilisateur.getRole() + ")");
        }

        // Écran affiché par défaut à l'ouverture : le tableau de bord
        ouvrirDashboard();
    }

    @FXML
    private void ouvrirDashboard() {
        chargerContenu("/fxml/dashboard.fxml", boutonDashboard);
    }

    @FXML
    private void ouvrirDossiers() {
        chargerContenu("/fxml/dossier.fxml", boutonDossiers);
    }

    @FXML
    private void ouvrirLois() {
        chargerContenu("/fxml/loi.fxml", boutonLois);
    }

    @FXML
    private void ouvrirAudiences() {
        chargerContenu("/fxml/audience.fxml", boutonAudiences);
    }

    @FXML
    private void ouvrirAssistantIA() {
        chargerContenu("/fxml/assistant_ia.fxml", boutonAssistantIA);
    }

    @FXML
    private void ouvrirEntrainementIA() {
        chargerContenu("/fxml/entrainement_ia.fxml", boutonEntrainementIA);
    }

    /** Se déconnecte et revient à l'écran de connexion (celui-ci reprend toute la fenêtre). */
    @FXML
    private void seDeconnecter() throws IOException {
        SessionUtilisateur.deconnecter();
        Main.changerEcran("/fxml/login.fxml");
        Main.getStagePrincipal().setTitle("LegalAssist Madagascar AI - Connexion");
    }

    /**
     * Charge un écran (FXML) dans la zone de contenu à droite, et met en
     * surbrillance le bouton de navigation correspondant dans la sidebar.
     */
    private void chargerContenu(String cheminFXML, Button boutonActif) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Node contenu = loader.load();
            zoneContenu.getChildren().setAll(contenu);
            mettreEnSurbrillance(boutonActif);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de " + cheminFXML + " : " + e.getMessage());
        }
    }

    /** Retire le style "actif" de tous les boutons de navigation, puis l'ajoute au bouton cliqué. */
    private void mettreEnSurbrillance(Button boutonActif) {
        for (Button bouton : new Button[]{boutonDashboard, boutonDossiers, boutonLois,
                boutonAudiences, boutonAssistantIA, boutonEntrainementIA}) {
            bouton.getStyleClass().remove("nav-bouton-actif");
        }
        if (!boutonActif.getStyleClass().contains("nav-bouton-actif")) {
            boutonActif.getStyleClass().add("nav-bouton-actif");
        }
    }
}
