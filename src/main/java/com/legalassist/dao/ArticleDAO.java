package com.legalassist.dao;

import com.legalassist.database.DatabaseConnection;
import com.legalassist.model.ArticleLoi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * CLASSE ArticleDAO
 * ============================================================
 * Gère les opérations SQL sur la table "article_loi".
 * C'est cette table que notre IA locale (TextClassifier) va
 * interroger pour trouver les articles pertinents grâce aux
 * mots-clés.
 * ============================================================
 */
public class ArticleDAO {

    /** Ajoute un nouvel article de loi. */
    public boolean ajouter(ArticleLoi article) {
        String sql = "INSERT INTO article_loi (loi_id, numero_article, contenu, mots_cles) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, article.getLoiId());
            stmt.setString(2, article.getNumeroArticle());
            stmt.setString(3, article.getContenu());
            stmt.setString(4, article.getMotsCles());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur ajouter ArticleLoi : " + e.getMessage());
            return false;
        }
    }

    /**
     * Retourne TOUS les articles de la base, avec leurs mots-clés.
     * Notre classe IA (TextClassifier) charge cette liste une fois
     * puis compare le texte de l'utilisateur avec les mots-clés,
     * entièrement en mémoire (donc rapide et sans Internet).
     */
    public List<ArticleLoi> listerTous() {
        List<ArticleLoi> resultats = new ArrayList<>();
        String sql = "SELECT * FROM article_loi";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultats.add(mapper(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur listerTous ArticleLoi : " + e.getMessage());
        }
        return resultats;
    }

    private ArticleLoi mapper(ResultSet rs) throws SQLException {
        ArticleLoi a = new ArticleLoi();
        a.setId(rs.getInt("id"));
        a.setLoiId(rs.getInt("loi_id"));
        a.setNumeroArticle(rs.getString("numero_article"));
        a.setContenu(rs.getString("contenu"));
        a.setMotsCles(rs.getString("mots_cles"));
        return a;
    }
}
