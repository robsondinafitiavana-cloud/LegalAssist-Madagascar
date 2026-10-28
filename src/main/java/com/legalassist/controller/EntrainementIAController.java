package com.legalassist.controller;

import com.legalassist.ai.TextClassifier;
import com.legalassist.ai.TrainingModel;
import com.legalassist.dao.MotCleDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CLASSE EntrainementIAController
 * ============================================================
 * Contrôleur de l'écran "Entraînement IA" (entrainement_ia.fxml).
 * Permet à l'administrateur :
 *  1) d'ajouter manuellement un exemple (texte + catégorie)
 *  2) de charger en une fois un fichier CSV complet
 *     (training_data.csv) pour enrichir l'IA
 *
 * IMPORTANT (pédagogie) : l'IA (TextClassifier) utilisée ici est
 * la MÊME instance que celle utilisée par l'écran "Assistant IA"
 * (grâce à TextClassifier.getInstance(), voir la classe
 * TextClassifier). Donc tout ce que tu entraînes ici est
 * immédiatement utilisable dans l'Assistant IA, sans redémarrer
 * l'application.
 *
 * DEPUIS L'AJOUT DE MotCleDAO : chaque mot-clé appris (ici ou via
 * CSV) est désormais enregistré de façon PERMANENTE dans la table
 * mot_cle_ia. Au prochain démarrage de l'application,
 * TextClassifier recharge automatiquement tout ce qui a été
 * appris précédemment (voir TextClassifier.chargerMotsClesPersistants()).
 * ============================================================
 */
public class EntrainementIAController {

    @FXML private TextField champTexteExemple;
    @FXML private ComboBox<String> comboCategorieExemple;
    @FXML private Label labelMessageExemple;

    @FXML private Label labelMessageCSV;
    @FXML private TextArea zoneStatistiques;

    private final TextClassifier classifier = TextClassifier.getInstance();
    private final TrainingModel trainingModel = new TrainingModel(classifier);
    private final MotCleDAO motCleDAO = new MotCleDAO();

    @FXML
    public void initialize() {
        comboCategorieExemple.setItems(javafx.collections.FXCollections.observableArrayList(
                "Droit civil", "Droit pénal", "Droit du travail",
                "Droit foncier", "Droit administratif", "Procédure judiciaire", "Droit de la famille"
        ));

        afficherStatistiques();
    }

    /** Appelé quand on clique sur "Ajouter cet exemple". */
    @FXML
    private void ajouterExemple() {
        String texte = champTexteExemple.getText();
        String categorie = comboCategorieExemple.getValue();

        if (texte == null || texte.isBlank() || categorie == null) {
            labelMessageExemple.setText("Renseigne un texte et choisis une catégorie.");
            return;
        }

        List<String> motsAjoutes = classifier.ajouterExempleEntrainement(texte, categorie);
        for (String mot : motsAjoutes) {
            motCleDAO.ajouter(categorie, mot);
        }

        labelMessageExemple.setText("Exemple ajouté et enregistré définitivement dans la catégorie \"" + categorie + "\".");
        champTexteExemple.clear();
        comboCategorieExemple.setValue(null);

        afficherStatistiques();
    }

    /** Appelé quand on clique sur "Charger un fichier CSV". */
    @FXML
    private void chargerFichierCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le fichier training_data.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));

        File fichier = fileChooser.showOpenDialog(champTexteExemple.getScene().getWindow());
        if (fichier == null) {
            return; // annulé par l'utilisateur
        }

        int nombreLignes = trainingModel.chargerDataset(fichier.getAbsolutePath());

        if (nombreLignes > 0) {
            labelMessageCSV.setText(nombreLignes + " ligne(s) d'entraînement chargée(s) avec succès depuis " + fichier.getName() + ".");
        } else {
            labelMessageCSV.setText("Aucune ligne chargée. Vérifie le format du fichier (colonnes : texte,categorie).");
        }

        afficherStatistiques();
    }

    /** Met à jour la zone de texte listant le nombre de mots-clés connus par catégorie. */
    private void afficherStatistiques() {
        Map<String, Integer> stats = classifier.getStatistiquesParCategorie();

        StringBuilder texte = new StringBuilder();
        texte.append("État actuel de l'IA (mots-clés appris par catégorie) :\n\n");

        for (Map.Entry<String, Integer> entree : stats.entrySet()) {
            texte.append(" - ").append(entree.getKey()).append(" : ").append(entree.getValue()).append(" mot(s)-clé(s)\n");
        }

        zoneStatistiques.setText(texte.toString());
    }
}
