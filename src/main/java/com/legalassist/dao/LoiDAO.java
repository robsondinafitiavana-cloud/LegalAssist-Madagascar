package com.legalassist.dao;

import com.legalassist.database.DatabaseConnection;
import com.legalassist.model.Loi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * CLASSE LoiDAO
 * ============================================================
 * Gère les opérations SQL sur la table "loi".
 * ============================================================
 */
public class LoiDAO {

    /** Ajoute une nouvelle loi. Retourne l'id généré (ou -1 en cas d'erreur). */
    public int ajouter(Loi loi) {
        String sql = "INSERT INTO loi (nom, domaine, date_publication, source) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loi.getNom());
            stmt.setString(2, loi.getDomaine());
            stmt.setDate(3, loi.getDatePublication() != null ? Date.valueOf(loi.getDatePublication()) : null);
            stmt.setString(4, loi.getSource());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erreur ajouter Loi : " + e.getMessage());
        }
        return -1;
    }

    /** Retourne toutes les lois enregistrées. */
    public List<Loi> listerTous() {
        List<Loi> resultats = new ArrayList<>();
        String sql = "SELECT * FROM loi ORDER BY nom";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultats.add(mapper(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur listerTous Loi : " + e.getMessage());
        }
        return resultats;
    }

    /** Retourne les lois d'un domaine juridique précis (ex: "Droit foncier"). */
    public List<Loi> trouverParDomaine(String domaine) {
        List<Loi> resultats = new ArrayList<>();
        String sql = "SELECT * FROM loi WHERE domaine = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, domaine);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultats.add(mapper(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur trouverParDomaine Loi : " + e.getMessage());
        }
        return resultats;
    }

    /** Compte le nombre total de lois enregistrées (tableau de bord). */
    public int compterTous() {
        String sql = "SELECT COUNT(*) FROM loi";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur compterTous Loi : " + e.getMessage());
        }
        return 0;
    }

    private Loi mapper(ResultSet rs) throws SQLException {
        Loi loi = new Loi();
        loi.setId(rs.getInt("id"));
        loi.setNom(rs.getString("nom"));
        loi.setDomaine(rs.getString("domaine"));
        Date date = rs.getDate("date_publication");
        loi.setDatePublication(date != null ? date.toLocalDate() : null);
        loi.setSource(rs.getString("source"));
        return loi;
    }
}
