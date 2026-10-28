package com.legalassist.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ============================================================
 * CLASSE DatabaseConnection
 * ============================================================
 * Cette classe centralise la connexion à la base de données
 * PostgreSQL. Toutes les classes DAO passent par ici pour
 * obtenir une connexion.
 *
 * IMPORTANT (débutant) :
 * - "hors ligne" veut dire que l'application n'a pas besoin
 *   d'Internet. Mais PostgreSQL doit quand même être installé
 *   et démarré SUR LA MACHINE LOCALE (ou sur le réseau local).
 *   On ne se connecte donc jamais à un serveur distant sur
 *   Internet, seulement à "localhost".
 *
 * - Pense à modifier URL, USER et PASSWORD selon ton
 *   installation PostgreSQL.
 * ============================================================
 */
public class DatabaseConnection {

    // Configuration PostgreSQL : variables d'environnement ou valeurs locales par défaut.
private static final String URL = System.getenv().getOrDefault(
        "LEGALASSIST_DB_URL",
        "jdbc:postgresql://localhost:5432/legalassist_db"
);

private static final String USER = System.getenv().getOrDefault(
        "LEGALASSIST_DB_USER",
        "postgres"
);

private static final String PASSWORD = System.getenv().getOrDefault(
        "LEGALASSIST_DB_PASSWORD",
        ""
);

    // On garde une seule connexion réutilisée dans toute l'application
    private static Connection connection;

    /**
     * Constructeur privé : on empêche de créer des instances de cette
     * classe car on veut qu'elle soit utilisée uniquement de façon
     * statique (comme un utilitaire global).
     */
    private DatabaseConnection() {
    }

    /**
     * Retourne une connexion active à la base de données.
     * Si aucune connexion n'existe encore, on en crée une nouvelle.
     *
     * @return la connexion JDBC vers PostgreSQL
     * @throws SQLException si la connexion échoue (ex: PostgreSQL éteint)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Ferme proprement la connexion (à appeler quand on quitte l'application).
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

    /**
     * Petite méthode de test : permet de vérifier rapidement si la
     * connexion à PostgreSQL fonctionne (utile pour déboguer).
     */
    public static boolean testerConnexion() {
        try {
            Connection c = getConnection();
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("Impossible de se connecter à PostgreSQL : " + e.getMessage());
            return false;
        }
    }
}
