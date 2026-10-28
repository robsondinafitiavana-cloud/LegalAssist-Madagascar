package com.legalassist.ai;

import com.legalassist.dao.MotCleDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * ============================================================
 * CLASSE TrainingModel
 * ============================================================
 * Permet à l'administrateur d'ENTRAÎNER l'IA à partir d'un
 * fichier "training_data.csv" au format :
 *
 *      texte,categorie
 *      vol telephone,droit penal
 *      terrain conteste,droit foncier
 *      licenciement abusif,droit travail
 *
 * Chaque ligne du fichier est ajoutée au dictionnaire du
 * TextClassifier grâce à ajouterExempleEntrainement(...), ET
 * enregistrée de façon PERMANENTE en base via MotCleDAO — sinon
 * cet entraînement serait perdu à la fermeture de l'application.
 *
 * ÉVOLUTION POSSIBLE : cette classe peut être remplacée par un
 * vrai entraînement Weka (ex: NaiveBayes, J48...) si tu veux
 * aller plus loin. La signature publique (chargerDataset) resterait
 * la même, donc le reste de l'application n'aurait pas à changer.
 * ============================================================
 */
public class TrainingModel {

    private final TextClassifier classifier;
    private final MotCleDAO motCleDAO = new MotCleDAO();

    public TrainingModel(TextClassifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Lit le fichier CSV ligne par ligne, alimente le classifieur EN
     * MÉMOIRE, et enregistre chaque mot-clé appris EN BASE DE DONNÉES
     * pour qu'il soit rechargé automatiquement au prochain démarrage.
     *
     * @param cheminFichier chemin vers training_data.csv
     * @return le nombre de lignes d'entraînement chargées avec succès
     */
    public int chargerDataset(String cheminFichier) {
        int nombreLignesChargees = 0;

        try (BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            boolean premiereLigne = true;

            while ((ligne = lecteur.readLine()) != null) {
                // On ignore la ligne d'en-tête "texte,categorie"
                if (premiereLigne) {
                    premiereLigne = false;
                    continue;
                }
                if (ligne.isBlank()) {
                    continue;
                }

                // Découpage simple sur la première virgule uniquement
                // (au cas où le texte contiendrait lui-même une virgule)
                int indexVirgule = ligne.lastIndexOf(',');
                if (indexVirgule == -1) {
                    continue; // ligne mal formée, on l'ignore
                }

                String texte = ligne.substring(0, indexVirgule).trim();
                String categorie = ligne.substring(indexVirgule + 1).trim();

                // ajouterExempleEntrainement met à jour l'IA en mémoire
                // ET nous renvoie les mots ajoutés, pour qu'on les
                // enregistre aussi de façon permanente ci-dessous.
                List<String> motsAjoutes = classifier.ajouterExempleEntrainement(texte, categorie);
                for (String mot : motsAjoutes) {
                    motCleDAO.ajouter(categorie, mot);
                }

                nombreLignesChargees++;
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du dataset : " + e.getMessage());
        }

        return nombreLignesChargees;
    }
}
