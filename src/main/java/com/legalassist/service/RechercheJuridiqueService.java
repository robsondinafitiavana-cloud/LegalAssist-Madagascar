package com.legalassist.service;

import com.legalassist.ai.TextClassifier;
import com.legalassist.dao.ArticleDAO;
import com.legalassist.dao.LoiDAO;
import com.legalassist.model.ArticleLoi;
import com.legalassist.model.Loi;

import java.util.List;

/**
 * ============================================================
 * CLASSE RechercheJuridiqueService
 * ============================================================
 * Fonction 2 du cahier des charges : "Recherche intelligente
 * des lois". Permet de chercher des lois et articles à partir
 * d'un mot-clé ou d'une phrase libre (ex: "licenciement injustifié").
 * ============================================================
 */
public class RechercheJuridiqueService {

    private final LoiDAO loiDAO;
    private final ArticleDAO articleDAO;
    private final TextClassifier classifier;

    public RechercheJuridiqueService() {
        this.loiDAO = new LoiDAO();
        this.articleDAO = new ArticleDAO();
        this.classifier = TextClassifier.getInstance();
    }

    /** Retourne toutes les lois enregistrées (pour l'écran "Gestion des lois"). */
    public List<Loi> listerToutesLesLois() {
        return loiDAO.listerTous();
    }

    /**
     * Recherche intelligente : à partir d'un texte libre, retourne
     * les articles de loi les plus pertinents, triés par pertinence.
     */
    public List<ArticleLoi> rechercherArticles(String texteRecherche) {
        List<ArticleLoi> tousLesArticles = articleDAO.listerTous();
        return classifier.rechercherArticlesPertinents(texteRecherche, tousLesArticles);
    }

    /** Retourne les lois appartenant à un domaine précis. */
    public List<Loi> rechercherParDomaine(String domaine) {
        return loiDAO.trouverParDomaine(domaine);
    }
}
