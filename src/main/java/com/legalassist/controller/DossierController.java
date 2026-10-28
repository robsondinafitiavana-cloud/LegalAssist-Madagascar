package com.legalassist.controller;

import com.legalassist.dao.DossierDAO;
import com.legalassist.model.Dossier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

/**
 * ============================================================
 * CLASSE DossierController
 * ============================================================
 * Contrôleur de l'écran "Gestion des dossiers" (dossier.fxml).
 * Permet d'ajouter, rechercher et afficher les dossiers
 * juridiques dans un tableau (TableView).
 * ============================================================
 */
public class DossierController {

    // --- Champs de saisie pour créer un dossier ---
    @FXML private TextField champNumero;
    @FXML private TextField champTitre;
    @FXML private TextArea champDescription;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private TextField champRecherche;
    @FXML private Label labelMessage;

    // --- Tableau d'affichage des dossiers ---
    @FXML private TableView<Dossier> tableDossiers;
    @FXML private TableColumn<Dossier, String> colNumero;
    @FXML private TableColumn<Dossier, String> colTitre;
    @FXML private TableColumn<Dossier, String> colCategorie;
    @FXML private TableColumn<Dossier, String> colStatut;

    private final DossierDAO dossierDAO = new DossierDAO();

    /**
     * Méthode appelée AUTOMATIQUEMENT par JavaFX juste après le
     * chargement du FXML (grâce à l'interface implicite Initializable
     * ou simplement une méthode nommée "initialize" — JavaFX la
     * détecte automatiquement si l'écran a un fx:controller défini).
     */
    @FXML
    public void initialize() {
        // On indique à chaque colonne du tableau quel attribut de Dossier afficher
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        comboCategorie.setItems(FXCollections.observableArrayList(
                "Droit civil", "Droit pénal", "Droit du travail",
                "Droit foncier", "Droit administratif", "Procédure judiciaire", "Droit de la famille"
        ));

        rafraichirTableau();
    }

    /** Recharge la liste des dossiers depuis la base et met à jour le tableau. */
    private void rafraichirTableau() {
        ObservableList<Dossier> liste = FXCollections.observableArrayList(dossierDAO.listerTous());
        tableDossiers.setItems(liste);
    }

    /** Appelé quand on clique sur "Ajouter le dossier". */
    @FXML
    private void ajouterDossier() {
        String numero = champNumero.getText();
        String titre = champTitre.getText();
        String description = champDescription.getText();
        String categorie = comboCategorie.getValue();

        if (numero == null || numero.isBlank() || titre == null || titre.isBlank()) {
            labelMessage.setText("Le numéro et le titre sont obligatoires.");
            return;
        }

        Dossier dossier = new Dossier();
        dossier.setNumero(numero);
        dossier.setTitre(titre);
        dossier.setDescription(description);
        dossier.setCategorie(categorie);
        dossier.setDateCreation(LocalDate.now());
        dossier.setStatut("EN_COURS");

        // On associe le dossier à l'avocat actuellement connecté
        var utilisateur = SessionUtilisateur.getUtilisateurConnecte();
        dossier.setAvocatResponsableId(utilisateur != null ? utilisateur.getId() : 0);

        boolean succes = dossierDAO.ajouter(dossier);

        if (succes) {
            labelMessage.setText("Dossier ajouté avec succès.");
            viderChamps();
            rafraichirTableau();
        } else {
            labelMessage.setText("Erreur lors de l'ajout du dossier.");
        }
    }

    /** Appelé quand on clique sur "Rechercher". */
    @FXML
    private void rechercherDossiers() {
        String motCle = champRecherche.getText();
        if (motCle == null || motCle.isBlank()) {
            rafraichirTableau();
            return;
        }
        ObservableList<Dossier> resultats = FXCollections.observableArrayList(dossierDAO.rechercher(motCle));
        tableDossiers.setItems(resultats);
    }

    /** Appelé quand on clique sur "Archiver" (le dossier sélectionné dans le tableau). */
    @FXML
    private void archiverDossierSelectionne() {
        Dossier selection = tableDossiers.getSelectionModel().getSelectedItem();
        if (selection == null) {
            labelMessage.setText("Sélectionnez d'abord un dossier dans le tableau.");
            return;
        }
        boolean succes = dossierDAO.archiver(selection.getId());
        labelMessage.setText(succes ? "Dossier archivé." : "Erreur lors de l'archivage.");
        rafraichirTableau();
    }

    private void viderChamps() {
        champNumero.clear();
        champTitre.clear();
        champDescription.clear();
        comboCategorie.setValue(null);
    }
}
