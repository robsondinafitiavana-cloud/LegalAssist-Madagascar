package com.legalassist.controller;

import com.legalassist.dao.DossierDAO;
import com.legalassist.model.AnalyseIA;
import com.legalassist.model.ArticleLoi;
import com.legalassist.model.Dossier;
import com.legalassist.service.IAService;
import com.legalassist.service.PDFService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * ============================================================
 * CLASSE IAController
 * ============================================================
 * Contrôleur de l'écran "Assistant IA" (assistant_ia.fxml).
 * C'est ici que l'utilisateur tape la description d'une affaire
 * et obtient l'analyse générée par l'IA locale, avec la
 * possibilité d'exporter un rapport PDF.
 * ============================================================
 */
public class IAController {

    @FXML private TextField champNumeroDossier;
    @FXML private TextArea champDescriptionAffaire;
    @FXML private TextArea zoneResultatAnalyse;
    @FXML private Label labelMessage;

    private final IAService iaService = new IAService();
    private final PDFService pdfService = new PDFService();
    private final DossierDAO dossierDAO = new DossierDAO();

    // On garde la dernière analyse en mémoire pour pouvoir l'exporter en PDF ensuite
    private AnalyseIA derniereAnalyse;
    private Dossier dossierAnalyse;

    /** Appelé quand on clique sur "Analyser avec l'IA". */
    @FXML
    private void analyserAvecIA() {
        String description = champDescriptionAffaire.getText();

        if (description == null || description.isBlank()) {
            labelMessage.setText("Veuillez saisir une description de l'affaire.");
            return;
        }

        // Fonction 1 + 2 + 3 réunies dans IAService.analyserDossier(...)
        derniereAnalyse = iaService.analyserDossier(description);

        // On construit un dossier "temporaire" en mémoire pour le rapport PDF
        // (si un numéro de dossier existant a été saisi, on le récupère en base)
        String numero = champNumeroDossier.getText();
        dossierAnalyse = new Dossier();
        dossierAnalyse.setNumero(numero != null && !numero.isBlank() ? numero : "SANS-NUMERO");
        dossierAnalyse.setTitre("Analyse IA");
        dossierAnalyse.setDescription(description);

        afficherResultat(derniereAnalyse);
    }

    /** Affiche le résultat de l'analyse dans la zone de texte. */
    private void afficherResultat(AnalyseIA analyse) {
        StringBuilder texte = new StringBuilder();

        texte.append("Domaine juridique : ").append(analyse.getDomaineJuridique()).append("\n");
        texte.append("Confiance de l'IA : ").append(Math.round(analyse.getScoreConfiance() * 100)).append("%\n\n");

        texte.append(analyse.getResume()).append("\n");

        texte.append("Articles de loi correspondants :\n");
        if (analyse.getArticlesTrouves() == null || analyse.getArticlesTrouves().isEmpty()) {
            texte.append(" - Aucun article trouvé dans la base actuelle.\n");
        } else {
            for (ArticleLoi article : analyse.getArticlesTrouves()) {
                texte.append(" - Article ").append(article.getNumeroArticle())
                        .append(" : ").append(article.getContenu()).append("\n");
            }
        }

        zoneResultatAnalyse.setText(texte.toString());
        labelMessage.setText("Analyse terminée.");
    }

    /** Appelé quand on clique sur "Exporter en PDF". */
    @FXML
    private void exporterPDF() {
        if (derniereAnalyse == null) {
            labelMessage.setText("Veuillez d'abord lancer une analyse.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.setInitialFileName("rapport_audience.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));

        File fichier = fileChooser.showSaveDialog(champDescriptionAffaire.getScene().getWindow());
        if (fichier == null) {
            return; // l'utilisateur a annulé
        }

        try {
            pdfService.genererRapportAudience(dossierAnalyse, derniereAnalyse, fichier.getAbsolutePath());
            labelMessage.setText("Rapport PDF généré : " + fichier.getName());
        } catch (IOException e) {
            Alert alerte = new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération du PDF : " + e.getMessage());
            alerte.showAndWait();
        }
    }
}
