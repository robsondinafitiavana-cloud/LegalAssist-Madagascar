package com.legalassist.service;

import com.legalassist.dao.ArticleDAO;
import com.legalassist.model.ArticleLoi;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ============================================================
 * CLASSE ImportLoiService
 * ============================================================
 * Implémente la Fonction 8 du cahier des charges : "Module
 * d'importation des lois" (PDF -> extraction -> découpage en
 * articles -> mots-clés -> insertion PostgreSQL).
 *
 * PRINCIPE (en 4 étapes, comme demandé dans le cahier des charges) :
 *   1) Extraction du texte brut du PDF (Apache PDFBox)
 *   2) Découpage du texte en articles, en repérant les motifs
 *      "Article 12", "Art. 12", etc.
 *   3) Détection automatique de mots-clés pour chaque article,
 *      en comptant les mots les plus fréquents (en excluant les
 *      mots "vides" comme "les", "dans", "pour"...)
 *   4) Insertion de chaque article dans la table article_loi
 *      via ArticleDAO (déjà existant)
 *
 * LIMITE HONNÊTE (pédagogie) : cette détection de mots-clés est
 * volontairement simple (fréquence des mots), pas une vraie
 * analyse sémantique. Pour un PDF bien structuré (articles
 * numérotés clairement), le résultat est utilisable tel quel ;
 * pour un PDF plus complexe, il est recommandé de relire et
 * corriger les articles importés depuis l'écran "Gestion des lois".
 * ============================================================
 */
public class ImportLoiService {

    private final ArticleDAO articleDAO = new ArticleDAO();

    /**
     * Mots français très courants à ignorer lors de la détection
     * de mots-clés (sinon "les", "dans", "pour"... ressortiraient
     * en tête de tous les articles, sans aucune valeur pour la
     * recherche).
     */
    private static final Set<String> MOTS_VIDES = Set.of(
            "les", "des", "que", "qui", "quoi", "pour", "dans", "avec", "est", "sont",
            "une", "un", "du", "de", "la", "le", "et", "ou", "ne", "pas", "par", "sur",
            "aux", "au", "ce", "ces", "cette", "cet", "son", "sa", "ses", "leur", "leurs",
            "tout", "tous", "toute", "toutes", "peut", "peuvent", "doit", "doivent",
            "etre", "fait", "faire", "ainsi", "selon", "dont", "il", "elle", "ils", "elles",
            "sans", "sous", "entre", "vers", "chez", "mais", "donc", "or", "ni", "car",
            "lorsque", "lorsqu", "meme", "autre", "autres", "tel", "telle", "telles",
            "cas", "alinea", "presente", "present"
    );

    /**
     * Importe tous les articles détectés dans un fichier PDF et les
     * rattache à une loi déjà existante en base (identifiée par loiId).
     *
     * @param fichierPDF le fichier PDF choisi par l'administrateur
     * @param loiId      l'id de la loi (déjà créée) à laquelle rattacher les articles
     * @return le nombre d'articles effectivement importés
     * @throws IOException si le fichier PDF ne peut pas être lu
     */
    public int importerDepuisPDF(File fichierPDF, int loiId) throws IOException {
        String texteComplet = extraireTexte(fichierPDF);
        List<String[]> articlesDetectes = decouperEnArticles(texteComplet);

        int compteur = 0;
        for (String[] article : articlesDetectes) {
            String numero = article[0];
            String contenu = article[1].trim();

            // On ignore les articles "vides" ou trop courts (probablement
            // du bruit de découpage plutôt qu'un vrai article de loi)
            if (contenu.length() < 15) {
                continue;
            }

            String motsCles = detecterMotsCles(contenu);

            ArticleLoi articleLoi = new ArticleLoi();
            articleLoi.setLoiId(loiId);
            articleLoi.setNumeroArticle(numero);
            articleLoi.setContenu(contenu);
            articleLoi.setMotsCles(motsCles);

            if (articleDAO.ajouter(articleLoi)) {
                compteur++;
            }
        }

        return compteur;
    }

    /** Étape 1 : extraction du texte brut du PDF avec Apache PDFBox. */
    private String extraireTexte(File fichier) throws IOException {
        try (PDDocument document = PDDocument.load(fichier)) {
            PDFTextStripper extracteur = new PDFTextStripper();
            return extracteur.getText(document);
        }
    }

    /**
     * Étape 2 : découpage du texte en articles.
     * On cherche toutes les occurrences de "Article X" ou "Art. X"
     * (insensible à la casse), et on considère que le texte entre
     * deux occurrences est le contenu de l'article correspondant.
     */
    private List<String[]> decouperEnArticles(String texte) {
        List<String[]> resultats = new ArrayList<>();

        Pattern motifArticle = Pattern.compile("(?i)\\b(article|art\\.?)\\s+(\\d+[a-z]?)\\b");
        Matcher matcher = motifArticle.matcher(texte);

        List<Integer> positionsDebut = new ArrayList<>();
        List<String> numerosArticles = new ArrayList<>();

        while (matcher.find()) {
            positionsDebut.add(matcher.start());
            numerosArticles.add("Art. " + matcher.group(2));
        }

        for (int i = 0; i < positionsDebut.size(); i++) {
            int debut = positionsDebut.get(i);
            int fin = (i + 1 < positionsDebut.size()) ? positionsDebut.get(i + 1) : texte.length();

            String contenuBrut = texte.substring(debut, fin);

            // On retire le "Article X" en tête du contenu (déjà stocké dans numerosArticles)
            contenuBrut = contenuBrut.replaceFirst("(?i)\\b(article|art\\.?)\\s+\\d+[a-z]?\\b", "");

            // On normalise les espaces et retours à la ligne multiples
            String contenuNettoye = contenuBrut.replaceAll("\\s+", " ").trim();

            resultats.add(new String[]{numerosArticles.get(i), contenuNettoye});
        }

        return resultats;
    }

    /**
     * Étape 3 : détection automatique de mots-clés.
     * Compte la fréquence de chaque mot significatif (plus de 4
     * lettres, hors mots vides) dans le contenu de l'article, et
     * garde les 6 mots les plus fréquents comme mots-clés.
     */
    private String detecterMotsCles(String contenu) {
        String texteNettoye = contenu.toLowerCase()
                .replaceAll("[^a-zàâäéèêëîïôöùûüç\\s]", " ");

        String[] mots = texteNettoye.split("\\s+");

        Map<String, Integer> frequenceParMot = new HashMap<>();
        for (String mot : mots) {
            if (mot.length() > 4 && !MOTS_VIDES.contains(mot)) {
                frequenceParMot.merge(mot, 1, Integer::sum);
            }
        }

        List<String> motsClesTries = new ArrayList<>(frequenceParMot.keySet());
        motsClesTries.sort((mot1, mot2) -> frequenceParMot.get(mot2) - frequenceParMot.get(mot1));

        int limite = Math.min(6, motsClesTries.size());
        return String.join(",", motsClesTries.subList(0, limite));
    }
}
