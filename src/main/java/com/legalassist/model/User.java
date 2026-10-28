package com.legalassist.model;

/**
 * ============================================================
 * CLASSE User
 * ============================================================
 * Cette classe représente un UTILISATEUR de l'application.
 * Un utilisateur peut être : Avocat, Juge ou Administrateur.
 *
 * En Java, une classe "modèle" (ou "entité") sert uniquement
 * à stocker des données. Elle correspond à une ligne dans la
 * table "utilisateur" de la base PostgreSQL.
 * ============================================================
 */
public class User {

    // ----- Attributs (les informations stockées sur l'utilisateur) -----
    private int id;
    private String nom;
    private String prenom;
    private String login;
    private String motDePasseHash; // mot de passe déjà haché (jamais en clair !)
    private String role;           // "AVOCAT", "JUGE" ou "ADMIN"

    /**
     * Constructeur vide.
     * Utile quand on va remplir les champs un par un avec les "setters".
     */
    public User() {
    }

    /**
     * Constructeur complet.
     * Permet de créer un objet User directement avec toutes ses infos.
     */
    public User(int id, String nom, String prenom, String login, String motDePasseHash, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
    }

    // ----- Getters et Setters -----
    // Ils permettent de lire (get) ou modifier (set) chaque attribut
    // depuis l'extérieur de la classe, en gardant les attributs "private".

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Méthode utilitaire pour vérifier facilement le rôle.
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isAvocat() {
        return "AVOCAT".equalsIgnoreCase(role);
    }

    public boolean isJuge() {
        return "JUGE".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + role + ")";
    }
}
