package com.legalassist.dao;

import com.legalassist.database.DatabaseConnection;
import com.legalassist.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * ============================================================
 * CLASSE UserDAO (Data Access Object)
 * ============================================================
 * Un "DAO" est une classe dont le SEUL rôle est de faire le
 * lien entre un objet Java (ici User) et la base de données :
 * - ajouter un utilisateur
 * - chercher un utilisateur
 * - vérifier un mot de passe
 *
 * On sépare bien ce code SQL du reste de l'application
 * (c'est le principe de l'architecture en couches / MVC).
 * ============================================================
 */
public class UserDAO {

    /**
     * Recherche un utilisateur par son login (nom d'utilisateur).
     * Retourne null si aucun utilisateur ne correspond.
     */
    public User trouverParLogin(String login) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapperResultatVersUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur trouverParLogin : " + e.getMessage());
        }
        return null; // aucun utilisateur trouvé
    }

    /**
     * Vérifie si le mot de passe saisi correspond au mot de passe
     * (haché) enregistré en base pour cet utilisateur.
     *
     * On utilise BCrypt : le mot de passe n'est JAMAIS stocké en
     * clair dans la base, seulement sa version "hachée".
     *
     * @param login          login saisi par l'utilisateur
     * @param motDePasseClair mot de passe saisi en clair
     * @return l'objet User si la connexion est valide, sinon null
     */
    public User authentifier(String login, String motDePasseClair) {
        User user = trouverParLogin(login);
        if (user == null) {
            return null; // login inconnu
        }
        boolean motDePasseCorrect = BCrypt.checkpw(motDePasseClair, user.getMotDePasseHash());
        return motDePasseCorrect ? user : null;
    }

    /**
     * Ajoute un nouvel utilisateur en base (le mot de passe est
     * haché automatiquement avant d'être enregistré).
     */
    public boolean ajouter(User user, String motDePasseClair) {
        String sql = "INSERT INTO utilisateur (nom, prenom, login, mot_de_passe_hash, role) VALUES (?, ?, ?, ?, ?)";

        String hash = BCrypt.hashpw(motDePasseClair, BCrypt.gensalt());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getLogin());
            stmt.setString(4, hash);
            stmt.setString(5, user.getRole());

            int lignesAffectees = stmt.executeUpdate();
            return lignesAffectees > 0;

        } catch (SQLException e) {
            System.err.println("Erreur ajouter User : " + e.getMessage());
            return false;
        }
    }

    /**
     * Transforme une ligne de résultat SQL (ResultSet) en objet User.
     * On évite de répéter ce code partout grâce à cette méthode privée.
     */
    private User mapperResultatVersUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setLogin(rs.getString("login"));
        user.setMotDePasseHash(rs.getString("mot_de_passe_hash"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
