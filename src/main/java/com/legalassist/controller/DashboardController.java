package com.legalassist.controller;

import com.legalassist.dao.AudienceDAO;
import com.legalassist.dao.DossierDAO;
import com.legalassist.dao.LoiDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * ============================================================
 * CLASSE DashboardController
 * ============================================================
 * Contrôleur de l'écran d'accueil (dashboard.fxml). Depuis la
 * mise en place de la sidebar (voir MainLayoutController), cet
 * écran n'a plus besoin de gérer la navigation : il se contente
 * d'afficher un message de bienvenue et quelques statistiques.
 * Il est chargé DANS la zone de contenu du layout principal.
 * ============================================================
 */
public class DashboardController {

    @FXML private Label labelBienvenue;
    @FXML private Label labelNombreDossiers;
    @FXML private Label labelNombreLois;
    @FXML private Label labelNombreAudiences;

    private final DossierDAO dossierDAO = new DossierDAO();
    private final LoiDAO loiDAO = new LoiDAO();
    private final AudienceDAO audienceDAO = new AudienceDAO();

    @FXML
    public void initialize() {
        var utilisateur = SessionUtilisateur.getUtilisateurConnecte();
        if (utilisateur != null) {
            labelBienvenue.setText("Bienvenue, " + utilisateur.getPrenom() + " " + utilisateur.getNom());
        }

        labelNombreDossiers.setText(String.valueOf(dossierDAO.compterTous()));
        labelNombreLois.setText(String.valueOf(loiDAO.compterTous()));
        labelNombreAudiences.setText(String.valueOf(audienceDAO.listerTous().size()));
    }
}
