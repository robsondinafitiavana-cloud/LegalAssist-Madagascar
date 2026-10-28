package com.legalassist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ============================================================
 * CLASSE Main — Point d'entrée de l'application JavaFX
 * ============================================================
 * C'est cette classe qui est lancée en premier. Elle affiche
 * la première fenêtre : l'écran de connexion (login.fxml).
 *
 * RAPPEL (débutant) :
 * - Une classe JavaFX doit hériter de "Application"
 * - La méthode start(Stage) est appelée automatiquement au
 *   démarrage : "Stage" = la fenêtre principale.
 * ============================================================
 */
public class Main extends Application {

    // Référence statique vers la fenêtre principale, pratique pour
    // changer d'écran (login -> dashboard) depuis n'importe quel contrôleur.
    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;

        stage.setTitle("LegalAssist Madagascar AI - Connexion");
        changerEcran("/fxml/login.fxml");

        stage.show();
    }

    /**
     * Méthode utilitaire appelée par les contrôleurs pour changer
     * d'écran (par exemple après une connexion réussie).
     *
     * @param cheminFXML chemin du fichier FXML, ex: "/fxml/dashboard.fxml"
     */
    public static void changerEcran(String cheminFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(cheminFXML));
        Parent racine = loader.load();

        Scene scene = new Scene(racine);

        // On applique la feuille de style CSS commune si elle existe
        java.net.URL css = Main.class.getResource("/css/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stagePrincipal.setScene(scene);

        // La fenêtre principale (avec la sidebar) a besoin de plus de place
        // que l'écran de connexion, qui reste plus petit et centré.
        if (cheminFXML.contains("main_layout")) {
            stagePrincipal.setMinWidth(1000);
            stagePrincipal.setMinHeight(650);
            stagePrincipal.setWidth(1200);
            stagePrincipal.setHeight(750);
        } else {
            stagePrincipal.setMinWidth(0);
            stagePrincipal.setMinHeight(0);
            stagePrincipal.sizeToScene();
        }

        stagePrincipal.centerOnScreen();
    }

    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }

    /**
     * Méthode main() classique : lance l'application JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
