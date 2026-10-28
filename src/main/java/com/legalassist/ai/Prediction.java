package com.legalassist.ai;

/**
 * ============================================================
 * CLASSE Prediction
 * ============================================================
 * Petit objet simple ("record-like") qui représente le résultat
 * d'une classification : la catégorie trouvée + le score de
 * confiance associé.
 * ============================================================
 */
public class Prediction {

    private final String categorie;
    private final double confiance; // valeur entre 0.0 et 1.0

    public Prediction(String categorie, double confiance) {
        this.categorie = categorie;
        this.confiance = confiance;
    }

    public String getCategorie() {
        return categorie;
    }

    public double getConfiance() {
        return confiance;
    }

    /** Retourne le pourcentage de confiance, arrondi, pour l'affichage (ex: "75%"). */
    public String getConfiancePourcentage() {
        return Math.round(confiance * 100) + "%";
    }

    @Override
    public String toString() {
        return categorie + " (confiance : " + getConfiancePourcentage() + ")";
    }
}
