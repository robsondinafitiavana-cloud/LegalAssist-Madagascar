package com.legalassist.service;

import com.legalassist.ai.Prediction;
import com.legalassist.ai.TextClassifier;
import com.legalassist.dao.ArticleDAO;
import com.legalassist.model.AnalyseIA;
import com.legalassist.model.ArticleLoi;

import java.util.List;

/**
 * ============================================================
 * CLASSE IAService
 * ============================================================
 * Cette classe fait le lien entre les CONTROLLERS (interface
 * graphique) et le "cerveau" de l'IA (package ai).
 * C'est elle qu'on appelle depuis IAController pour analyser
 * un dossier.
 *
 * Rôle d'un "service" en architecture MVC : contenir la
 * LOGIQUE MÉTIER (les règles de l'application), sans savoir
 * comment s'affiche l'interface graphique.
 * ============================================================
 */
public class IAService {

    private final TextClassifier classifier;
    private final ArticleDAO articleDAO;

    public IAService() {
        this.classifier = TextClassifier.getInstance();
        this.articleDAO = new ArticleDAO();
    }

    /**
     * Fonction 1 : Analyse complète d'une description d'affaire.
     * Combine classification + recherche des articles + résumé.
     *
     * @param descriptionAffaire texte libre décrivant l'affaire
     * @return un objet AnalyseIA regroupant tous les résultats
     */
    public AnalyseIA analyserDossier(String descriptionAffaire) {

        // Étape 1 : trouver le domaine juridique (ex: "Droit foncier")
        Prediction prediction = classifier.classifier(descriptionAffaire);

        // Étape 2 : chercher les articles de loi correspondants dans la base
        List<ArticleLoi> tousLesArticles = articleDAO.listerTous();
        List<ArticleLoi> articlesPertinents = classifier.rechercherArticlesPertinents(descriptionAffaire, tousLesArticles);

        // Étape 3 : générer un résumé automatique simple
        String resume = genererResume(descriptionAffaire);

        AnalyseIA analyse = new AnalyseIA();
        analyse.setDomaineJuridique(prediction.getCategorie());
        analyse.setCategorie(prediction.getCategorie());
        analyse.setScoreConfiance(prediction.getConfiance());
        analyse.setArticlesTrouves(articlesPertinents);
        analyse.setResume(resume);

        return analyse;
    }

    /**
     * Fonction 3 : Résumé automatique.
     * Version simple pour débutant : on découpe le texte en phrases
     * et on garde les 2 premières + une phrase de conclusion type.
     * (Peut être amélioré plus tard avec Apache OpenNLP pour un vrai
     * résumé statistique.)
     */
    public String genererResume(String texteComplet) {
        if (texteComplet == null || texteComplet.isBlank()) {
            return "Aucune description fournie.";
        }

        String[] phrases = texteComplet.split("(?<=[.!?])\\s+");

        StringBuilder resume = new StringBuilder();
        resume.append("Résumé de l'affaire :\n\n");
        resume.append("Cette affaire concerne : ");
        resume.append(phrases.length > 0 ? phrases[0].trim() : texteComplet.trim());
        resume.append("\n\n");

        if (phrases.length > 1) {
            resume.append("Points complémentaires : ");
            resume.append(phrases[1].trim());
            resume.append("\n");
        }

        return resume.toString();
    }
}
