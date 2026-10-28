package com.legalassist.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * ============================================================
 * CLASSE GenerateurMotDePasse (outil autonome, pas d'interface)
 * ============================================================
 * Petit programme à part, à exécuter UNE SEULE FOIS pour générer
 * le hash BCrypt du mot de passe du tout premier compte
 * administrateur, que tu colleras ensuite dans le script SQL
 * (table utilisateur, colonne mot_de_passe_hash).
 *
 * COMMENT L'UTILISER (débutant) :
 * 1) Dans NetBeans/IntelliJ, clique droit sur ce fichier
 *    -> "Run File" (ou "Exécuter").
 * 2) Le hash s'affiche dans la console.
 * 3) Copie-colle ce hash dans ta requête SQL d'insertion.
 *
 * Tu peux changer le mot de passe "admin123" ci-dessous par
 * celui que tu veux réellement utiliser.
 * ============================================================
 */
public class GenerateurMotDePasse {

    public static void main(String[] args) {
        String motDePasseEnClair = "admin123"; // <-- change ici si besoin

        String hash = BCrypt.hashpw(motDePasseEnClair, BCrypt.gensalt());

        System.out.println("Mot de passe en clair : " + motDePasseEnClair);
        System.out.println("Hash BCrypt à coller dans le script SQL :");
        System.out.println(hash);
    }
}
