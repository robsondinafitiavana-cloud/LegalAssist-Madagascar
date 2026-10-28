package com.legalassist.dao;

import com.legalassist.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CLASSE MotCleDAO
 * ============================================================
 * Gère la PERSISTANCE de l'entraînement de l'IA : chaque mot-clé
 * appris (via l'écran "Entraînement IA" ou l'import de CSV) est
 * enregistré ici, table "mot_cle_ia".
 *
 * Sans cette classe, tout l'apprentissage de l'IA (TextClassifier)
 * n'existait qu'en mémoire et disparaissait à la fermeture de
 * l'application. Maintenant, au démarrage, TextClassifier
 * recharge automatiquement tous les mots-clés stockés ici
 * (voir TextClassifier.chargerMotsClesPersistants()).
 * ============================================================
 */
public class MotCleDAO {

    /**
     * Enregistre un mot-clé pour une catégorie donnée. Si ce couple
     * (catégorie, mot-clé) existe déjà, on ne fait rien (ON CONFLICT
     * DO NOTHING) pour éviter les doublons.
     */
    public boolean ajouter(String categorie, String motCle) {
        String sql = "INSERT INTO mot_cle_ia (categorie, mot_cle) VALUES (?, ?) " +
                     "ON CONFLICT (categorie, mot_cle) DO NOTHING";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categorie);
            stmt.setString(2, motCle);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Erreur ajouter MotCleIA : " + e.getMessage());
            return false;
        }
    }

    /**
     * Retourne TOUS les mots-clés appris, regroupés par catégorie.
     * Utilisé au démarrage de l'application pour reconstituer
     * l'état de l'IA tel qu'il était avant la fermeture.
     */
    public Map<String, List<String>> listerTousParCategorie() {
        Map<String, List<String>> resultats = new LinkedHashMap<>();
        String sql = "SELECT categorie, mot_cle FROM mot_cle_ia";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String categorie = rs.getString("categorie");
                String motCle = rs.getString("mot_cle");
                resultats.computeIfAbsent(categorie, k -> new ArrayList<>()).add(motCle);
            }

        } catch (SQLException e) {
            System.err.println("Erreur listerTousParCategorie MotCleIA : " + e.getMessage());
        }
        return resultats;
    }
}
