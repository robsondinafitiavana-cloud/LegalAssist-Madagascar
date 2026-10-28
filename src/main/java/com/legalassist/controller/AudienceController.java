package com.legalassist.controller;

import com.legalassist.dao.AudienceDAO;
import com.legalassist.dao.DossierDAO;
import com.legalassist.model.Audience;
import com.legalassist.model.Dossier;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CLASSE AudienceController
 * ============================================================
 * Contrôleur de l'écran "Gestion des audiences" (audience.fxml).
 * Permet de planifier une audience pour un dossier existant
 * (date, lieu, parties, notes) et d'afficher la liste de toutes
 * les audiences planifiées.
 *
 * Construit sur le même modèle que DossierController et
 * LoiController.
 * ============================================================
 */
public class AudienceController {

    @FXML private ComboBox<Dossier> comboDossier;
    @FXML private DatePicker champDateAudience;
    @FXML private TextField champLieu;
    @FXML private TextField champDemandeur;
    @FXML private TextField champDefendeur;
    @FXML private TextArea champNotes;
    @FXML private Label labelMessage;

    @FXML private TableView<Audience> tableAudiences;
    @FXML private TableColumn<Audience, String> colDossier;
    @FXML private TableColumn<Audience, String> colDate;
    @FXML private TableColumn<Audience, String> colLieu;
    @FXML private TableColumn<Audience, String> colDemandeur;
    @FXML private TableColumn<Audience, String> colDefendeur;

    private final AudienceDAO audienceDAO = new AudienceDAO();
    private final DossierDAO dossierDAO = new DossierDAO();

    // Permet de retrouver rapidement le "numéro - titre" d'un dossier à partir de son id,
    // pour l'afficher dans le tableau au lieu d'un simple chiffre.
    private final Map<Integer, String> libelleDossierParId = new HashMap<>();

    @FXML
    public void initialize() {
        List<Dossier> dossiers = dossierDAO.listerTous();

        comboDossier.setItems(FXCollections.observableArrayList(dossiers));
        for (Dossier d : dossiers) {
            libelleDossierParId.put(d.getId(), d.getNumero() + " - " + d.getTitre());
        }

        // Colonnes simples : on lit directement l'attribut de l'objet Audience
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateAudience"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colDemandeur.setCellValueFactory(new PropertyValueFactory<>("demandeur"));
        colDefendeur.setCellValueFactory(new PropertyValueFactory<>("defendeur"));

        // Colonne "Dossier" : on affiche le libellé (numéro + titre) au lieu du simple id
        colDossier.setCellValueFactory(cellData -> {
            int dossierId = cellData.getValue().getDossierId();
            String libelle = libelleDossierParId.getOrDefault(dossierId, "Dossier #" + dossierId);
            return new SimpleStringProperty(libelle);
        });

        rafraichirTableau();
    }

    private void rafraichirTableau() {
        ObservableList<Audience> liste = FXCollections.observableArrayList(audienceDAO.listerTous());
        tableAudiences.setItems(liste);
    }

    /** Appelé quand on clique sur "Planifier l'audience". */
    @FXML
    private void ajouterAudience() {
        Dossier dossierSelectionne = comboDossier.getValue();

        if (dossierSelectionne == null) {
            labelMessage.setText("Choisis d'abord un dossier.");
            return;
        }
        if (champDateAudience.getValue() == null) {
            labelMessage.setText("Choisis une date d'audience.");
            return;
        }

        Audience audience = new Audience();
        audience.setDossierId(dossierSelectionne.getId());
        audience.setDateAudience(champDateAudience.getValue());
        audience.setLieu(champLieu.getText());
        audience.setDemandeur(champDemandeur.getText());
        audience.setDefendeur(champDefendeur.getText());
        audience.setNotes(champNotes.getText());

        boolean succes = audienceDAO.ajouter(audience);

        if (succes) {
            labelMessage.setText("Audience planifiée avec succès.");
            viderChamps();
            rafraichirTableau();
        } else {
            labelMessage.setText("Erreur lors de la planification de l'audience.");
        }
    }

    /** Appelé quand on clique sur "Supprimer la sélection". */
    @FXML
    private void supprimerAudienceSelectionnee() {
        Audience selection = tableAudiences.getSelectionModel().getSelectedItem();
        if (selection == null) {
            labelMessage.setText("Sélectionne d'abord une audience dans le tableau.");
            return;
        }
        boolean succes = audienceDAO.supprimer(selection.getId());
        labelMessage.setText(succes ? "Audience supprimée." : "Erreur lors de la suppression.");
        rafraichirTableau();
    }

    private void viderChamps() {
        comboDossier.setValue(null);
        champDateAudience.setValue(null);
        champLieu.clear();
        champDemandeur.clear();
        champDefendeur.clear();
        champNotes.clear();
    }
}
