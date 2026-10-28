package com.legalassist.controller;

import com.legalassist.model.User;

/**
 * ============================================================
 * CLASSE SessionUtilisateur
 * ============================================================
 * Petite classe "utilitaire" très simple qui garde en mémoire
 * l'utilisateur actuellement connecté, pour que TOUS les
 * contrôleurs puissent savoir "qui est connecté" et "quel est
 * son rôle" (Avocat / Juge / Administrateur), sans avoir à se
 * repasser l'objet User entre les écrans.
 * ============================================================
 */
public class SessionUtilisateur {

    private static User utilisateurConnecte;

    private SessionUtilisateur() {
    }

    public static void connecter(User user) {
        utilisateurConnecte = user;
    }

    public static User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static void deconnecter() {
        utilisateurConnecte = null;
    }

    public static boolean estConnecte() {
        return utilisateurConnecte != null;
    }
}
