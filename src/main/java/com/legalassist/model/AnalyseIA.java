package com.legalassist.model;

import java.util.List;

/**
 * ============================================================
 * CLASSE AnalyseIA
 * ============================================================
 * Cette classe ne représente PAS une table de la base de
 * données. C'est un simple "conteneur de résultats" : elle
 * regroupe tout ce que notre IA locale a trouvé après avoir
 * analysé la description d'un dossier.
 * ============================================================
 */
public class AnalyseIA {

    private String domaineJuridique;      // ex: "Droit foncier"
    private String categorie;             // ex: "Litige de propriété"
    private double scoreConfiance;        // entre 0 et 1 : à quel point l'IA est "sûre"
    private List<ArticleLoi> articlesTrouves; // articles de loi correspondants
    private String resume;                // résumé automatique généré

    public AnalyseIA() {
    }

    public AnalyseIA(String domaineJuridique, String categorie, double scoreConfiance,
                      List<ArticleLoi> articlesTrouves, String resume) {
        this.domaineJuridique = domaineJuridique;
        this.categorie = categorie;
        this.scoreConfiance = scoreConfiance;
        this.articlesTrouves = articlesTrouves;
        this.resume = resume;
    }

    public String getDomaineJuridique() { return domaineJuridique; }
    public void setDomaineJuridique(String domaineJuridique) { this.domaineJuridique = domaineJuridique; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getScoreConfiance() { return scoreConfiance; }
    public void setScoreConfiance(double scoreConfiance) { this.scoreConfiance = scoreConfiance; }

    public List<ArticleLoi> getArticlesTrouves() { return articlesTrouves; }
    public void setArticlesTrouves(List<ArticleLoi> articlesTrouves) { this.articlesTrouves = articlesTrouves; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
}
