package com.legalassist.dao;

import com.legalassist.database.DatabaseConnection;
import com.legalassist.model.Dossier;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * CLASSE DossierDAO
 * ============================================================
 * Gère toutes les opérations SQL liées à la table "dossier" :
 * ajouter, modifier, supprimer, rechercher, archiver.
 * ============================================================
 */
public class DossierDAO {

    /** Ajoute un nouveau dossier en base. */
    public boolean ajouter(Dossier d) {
        String sql = "INSERT INTO dossier (numero, titre, description, categorie, date_creation, statut, avocat_responsable) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNumero());
            stmt.setString(2, d.getTitre());
            stmt.setString(3, d.getDescription());
            stmt.setString(4, d.getCategorie());
            stmt.setDate(5, Date.valueOf(d.getDateCreation() != null ? d.getDateCreation() : LocalDate.now()));
            stmt.setString(6, d.getStatut() != null ? d.getStatut() : "EN_COURS");
            stmt.setInt(7, d.getAvocatResponsableId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur ajouter Dossier : " + e.getMessage());
            return false;
        }
    }

    /** Modifie un dossier existant. */
    public boolean modifier(Dossier d) {
        String sql = "UPDATE dossier SET titre = ?, description = ?, categorie = ?, statut = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getTitre());
            stmt.setString(2, d.getDescription());
            stmt.setString(3, d.getCategorie());
            stmt.setString(4, d.getStatut());
            stmt.setInt(5, d.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur modifier Dossier : " + e.getMessage());
            return false;
        }
    }

    /** Supprime définitivement un dossier. */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM dossier WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur supprimer Dossier : " + e.getMessage());
            return false;
        }
    }

    /** Passe le statut d'un dossier à "ARCHIVE" (sans le supprimer). */
    public boolean archiver(int id) {
        String sql = "UPDATE dossier SET statut = 'ARCHIVE' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur archiver Dossier : " + e.getMessage());
            return false;
        }
    }

    /** Recherche un dossier précis par son id. */
    public Dossier trouverParId(int id) {
        String sql = "SELECT * FROM dossier WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapper(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur trouverParId Dossier : " + e.getMessage());
        }
        return null;
    }

    /** Recherche des dossiers par mot-clé dans le titre ou la description. */
    public List<Dossier> rechercher(String motCle) {
        List<Dossier> resultats = new ArrayList<>();
        String sql = "SELECT * FROM dossier WHERE LOWER(titre) LIKE ? OR LOWER(description) LIKE ? ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String motif = "%" + motCle.toLowerCase() + "%";
            stmt.setString(1, motif);
            stmt.setString(2, motif);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultats.add(mapper(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur rechercher Dossier : " + e.getMessage());
        }
        return resultats;
    }

    /** Retourne la liste de TOUS les dossiers (pour affichage dans un tableau). */
    public List<Dossier> listerTous() {
        List<Dossier> resultats = new ArrayList<>();
        String sql = "SELECT * FROM dossier ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultats.add(mapper(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur listerTous Dossier : " + e.getMessage());
        }
        return resultats;
    }

    /** Compte le nombre total de dossiers (utile pour le tableau de bord). */
    public int compterTous() {
        String sql = "SELECT COUNT(*) FROM dossier";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur compterTous Dossier : " + e.getMessage());
        }
        return 0;
    }

    /** Convertit une ligne SQL en objet Dossier. */
    private Dossier mapper(ResultSet rs) throws SQLException {
        Dossier d = new Dossier();
        d.setId(rs.getInt("id"));
        d.setNumero(rs.getString("numero"));
        d.setTitre(rs.getString("titre"));
        d.setDescription(rs.getString("description"));
        d.setCategorie(rs.getString("categorie"));
        Date date = rs.getDate("date_creation");
        d.setDateCreation(date != null ? date.toLocalDate() : null);
        d.setStatut(rs.getString("statut"));
        d.setAvocatResponsableId(rs.getInt("avocat_responsable"));
        return d;
    }
}
