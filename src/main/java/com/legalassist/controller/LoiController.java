package com.legalassist.controller;

import com.legalassist.dao.ArticleDAO;
import com.legalassist.dao.LoiDAO;
import com.legalassist.model.ArticleLoi;
import com.legalassist.model.Loi;
import com.legalassist.service.ImportLoiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

/**
 * ============================================================
 * CLASSE LoiController
 * ============================================================
 * Contrôleur de l'écran "Gestion des lois" (loi.fxml).
 * Permet à l'administrateur d'ajouter des lois et leurs
 * articles (avec mots-clés), et de voir la liste des lois
 * déjà enregistrées.
 *
 * Construit sur le même modèle que DossierController, pour
 * que tu puisses comparer les deux et bien comprendre le
 * schéma général : @FXML relie les composants, un DAO lit/écrit
 * en base, un TableView affiche les résultats.
 * ============================================================
 */
public class LoiController {

    // --- Formulaire "Ajouter une loi" ---
    @FXML private TextField champNomLoi;
    @FXML private ComboBox<String> comboDomaine;
    @FXML private DatePicker champDatePublication;
    @FXML private TextField champSource;
    @FXML private Label labelMessageLoi;

    // --- Formulaire "Ajouter un article" ---
    @FXML private ComboBox<Loi> comboLoiPourArticle;
    @FXML private TextField champNumeroArticle;
    @FXML private TextArea champContenuArticle;
    @FXML private TextField champMotsCles;
    @FXML private Label labelMessageArticle;

    // --- Import automatique depuis un PDF ---
    @FXML private ComboBox<Loi> comboLoiPourImportPDF;
    @FXML private Label labelMessageImportPDF;

    // --- Tableau des lois ---
    @FXML private TableView<Loi> tableLois;
    @FXML private TableColumn<Loi, String> colNomLoi;
    @FXML private TableColumn<Loi, String> colDomaine;
    @FXML private TableColumn<Loi, String> colSource;

    private final LoiDAO loiDAO = new LoiDAO();
    private final ArticleDAO articleDAO = new ArticleDAO();
    private final ImportLoiService importLoiService = new ImportLoiService();

    /** Appelée automatiquement par JavaFX après le chargement du FXML. */
    @FXML
    public void initialize() {
        colNomLoi.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDomaine.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));

        comboDomaine.setItems(FXCollections.observableArrayList(
                "Droit civil", "Droit pénal", "Droit du travail",
                "Droit foncier", "Droit administratif", "Procédure judiciaire", "Droit de la famille"
        ));

        rafraichirTout();
    }

    /** Recharge le tableau des lois ET les listes déroulantes "quelle loi pour...". */
    private void rafraichirTout() {
        ObservableList<Loi> liste = FXCollections.observableArrayList(loiDAO.listerTous());
        tableLois.setItems(liste);
        comboLoiPourArticle.setItems(liste);
        comboLoiPourImportPDF.setItems(liste);
    }

    /** Appelé quand on clique sur "Ajouter la loi". */
    @FXML
    private void ajouterLoi() {
        String nom = champNomLoi.getText();
        String domaine = comboDomaine.getValue();

        if (nom == null || nom.isBlank() || domaine == null) {
            labelMessageLoi.setText("Le nom et le domaine sont obligatoires.");
            return;
        }

        Loi loi = new Loi();
        loi.setNom(nom);
        loi.setDomaine(domaine);
        loi.setDatePublication(champDatePublication.getValue() != null ? champDatePublication.getValue() : LocalDate.now());
        loi.setSource(champSource.getText());

        int idGenere = loiDAO.ajouter(loi);

        if (idGenere > 0) {
            labelMessageLoi.setText("Loi ajoutée avec succès (id " + idGenere + ").");
            champNomLoi.clear();
            comboDomaine.setValue(null);
            champDatePublication.setValue(null);
            champSource.clear();
            rafraichirTout();
        } else {
            labelMessageLoi.setText("Erreur lors de l'ajout de la loi.");
        }
    }

    /** Appelé quand on clique sur "Ajouter l'article". */
    @FXML
    private void ajouterArticle() {
        Loi loiSelectionnee = comboLoiPourArticle.getValue();
        String numero = champNumeroArticle.getText();
        String contenu = champContenuArticle.getText();
        String motsCles = champMotsCles.getText();

        if (loiSelectionnee == null) {
            labelMessageArticle.setText("Choisissez d'abord une loi.");
            return;
        }
        if (numero == null || numero.isBlank() || contenu == null || contenu.isBlank()) {
            labelMessageArticle.setText("Le numéro et le contenu de l'article sont obligatoires.");
            return;
        }
        if (motsCles == null || motsCles.isBlank()) {
            labelMessageArticle.setText("Ajoute au moins un mot-clé (ex: terrain,propriete) pour que l'IA puisse retrouver cet article.");
            return;
        }

        ArticleLoi article = new ArticleLoi();
        article.setLoiId(loiSelectionnee.getId());
        article.setNumeroArticle(numero);
        article.setContenu(contenu);
        article.setMotsCles(motsCles);

        boolean succes = articleDAO.ajouter(article);

        if (succes) {
            labelMessageArticle.setText("Article ajouté avec succès à la loi \"" + loiSelectionnee.getNom() + "\".");
            champNumeroArticle.clear();
            champContenuArticle.clear();
            champMotsCles.clear();
        } else {
            labelMessageArticle.setText("Erreur lors de l'ajout de l'article.");
        }
    }

    /**
     * Appelé quand on clique sur "Importer un PDF". Ouvre un
     * sélecteur de fichier, extrait automatiquement les articles
     * du PDF choisi, détecte leurs mots-clés, et les enregistre
     * tous d'un coup dans la loi sélectionnée (voir ImportLoiService).
     */
    @FXML
    private void importerPDF() {
        Loi loiSelectionnee = comboLoiPourImportPDF.getValue();

        if (loiSelectionnee == null) {
            labelMessageImportPDF.setText("Choisis d'abord la loi à laquelle rattacher les articles importés.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le fichier PDF de la loi");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));

        File fichier = fileChooser.showOpenDialog(comboLoiPourImportPDF.getScene().getWindow());
        if (fichier == null) {
            return; // annulé par l'utilisateur
        }

        labelMessageImportPDF.setText("Import en cours, veuillez patienter...");

        try {
            int nombreArticlesImportes = importLoiService.importerDepuisPDF(fichier, loiSelectionnee.getId());

            if (nombreArticlesImportes > 0) {
                labelMessageImportPDF.setText(nombreArticlesImportes + " article(s) importé(s) avec succès depuis "
                        + fichier.getName() + ". Pense à vérifier les mots-clés détectés automatiquement.");
            } else {
                labelMessageImportPDF.setText("Aucun article détecté dans ce PDF. Vérifie qu'il contient bien des "
                        + "mentions \"Article X\" ou \"Art. X\" clairement identifiables.");
            }

        } catch (IOException e) {
            labelMessageImportPDF.setText("Erreur lors de la lecture du PDF : " + e.getMessage());
        }
    }
}
