package com.legalassist.dao;

import com.legalassist.database.DatabaseConnection;
import com.legalassist.model.Audience;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * CLASSE AudienceDAO
 * ============================================================
 * Gère les opérations SQL sur la table "audience".
 * Construite sur le même modèle que DossierDAO et LoiDAO.
 * ============================================================
 */
public class AudienceDAO {

    /** Ajoute une nouvelle audience liée à un dossier. */
    public boolean ajouter(Audience audience) {
        String sql = "INSERT INTO audience (dossier_id, date_audience, lieu, demandeur, defendeur, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, audience.getDossierId());
            stmt.setDate(2, audience.getDateAudience() != null ? Date.valueOf(audience.getDateAudience()) : null);
            stmt.setString(3, audience.getLieu());
            stmt.setString(4, audience.getDemandeur());
            stmt.setString(5, audience.getDefendeur());
            stmt.setString(6, audience.getNotes());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur ajouter Audience : " + e.getMessage());
            return false;
        }
    }

    /** Retourne toutes les audiences, triées par date. */
    public List<Audience> listerTous() {
        List<Audience> resultats = new ArrayList<>();
        String sql = "SELECT * FROM audience ORDER BY date_audience";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultats.add(mapper(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur listerTous Audience : " + e.getMessage());
        }
        return resultats;
    }

    /** Retourne les audiences liées à un dossier précis. */
    public List<Audience> listerParDossier(int dossierId) {
        List<Audience> resultats = new ArrayList<>();
        String sql = "SELECT * FROM audience WHERE dossier_id = ? ORDER BY date_audience";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dossierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultats.add(mapper(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur listerParDossier Audience : " + e.getMessage());
        }
        return resultats;
    }

    /** Supprime une audience. */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM audience WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur supprimer Audience : " + e.getMessage());
            return false;
        }
    }

    private Audience mapper(ResultSet rs) throws SQLException {
        Audience a = new Audience();
        a.setId(rs.getInt("id"));
        a.setDossierId(rs.getInt("dossier_id"));
        Date date = rs.getDate("date_audience");
        a.setDateAudience(date != null ? date.toLocalDate() : null);
        a.setLieu(rs.getString("lieu"));
        a.setDemandeur(rs.getString("demandeur"));
        a.setDefendeur(rs.getString("defendeur"));
        a.setNotes(rs.getString("notes"));
        return a;
    }
}
