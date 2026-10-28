package com.legalassist.service;

import com.legalassist.model.AnalyseIA;
import com.legalassist.model.ArticleLoi;
import com.legalassist.model.Dossier;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ============================================================
 * CLASSE PDFService
 * ============================================================
 * Génère le rapport PDF de préparation d'audience, en utilisant
 * la bibliothèque Apache PDFBox (100% locale, aucune connexion
 * Internet nécessaire pour générer le PDF).
 *
 * PRINCIPE PDFBOX (pour débutant) :
 * - On crée un PDDocument (le fichier PDF en mémoire)
 * - On ajoute des PDPage (des pages)
 * - Pour écrire dedans, on ouvre un PDPageContentStream et on
 *   utilise showText() pour écrire du texte, en indiquant à
 *   chaque fois la police et la position (x, y).
 * - À la fin, on appelle document.save(cheminFichier) puis
 *   document.close().
 * ============================================================
 */
public class PDFService {

    private static final float MARGE_GAUCHE = 50;
    private static final float LARGEUR_LIGNE = 500; // largeur utile pour le retour à la ligne automatique

    /**
     * Génère le rapport PDF de préparation d'audience et l'enregistre
     * sur le disque.
     *
     * @param dossier         le dossier concerné
     * @param analyse         le résultat de l'analyse IA
     * @param cheminSortie    chemin complet du fichier PDF à créer (ex: "rapports/dossier_12.pdf")
     * @throws IOException si l'écriture du fichier échoue
     */
    public void genererRapportAudience(Dossier dossier, AnalyseIA analyse, String cheminSortie) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font policeTitre = PDType1Font.HELVETICA_BOLD;
            PDType1Font policeNormale = PDType1Font.HELVETICA;

            PDPageContentStream contenu = new PDPageContentStream(document, page);

            float y = 780; // position verticale de départ (haut de page)

            // ----- En-tête -----
            y = ecrireTexte(contenu, policeTitre, 16, MARGE_GAUCHE, y, "LEGAL ASSIST MADAGASCAR AI");
            y = ecrireTexte(contenu, policeNormale, 12, MARGE_GAUCHE, y - 5, "Rapport de préparation d'audience");
            y -= 20;

            String dateDuJour = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            y = ecrireTexte(contenu, policeNormale, 10, MARGE_GAUCHE, y, "Dossier : " + dossier.getNumero() + " - " + dossier.getTitre());
            y = ecrireTexte(contenu, policeNormale, 10, MARGE_GAUCHE, y - 2, "Date : " + dateDuJour);
            y -= 15;

            // ----- Résumé -----
            y = ecrireSection(contenu, policeTitre, policeNormale, y, "Résumé :", analyse.getResume());

            // ----- Analyse IA -----
            String texteAnalyse = "Domaine juridique detecte : " + analyse.getDomaineJuridique() +
                    " (confiance : " + Math.round(analyse.getScoreConfiance() * 100) + "%)";
            y = ecrireSection(contenu, policeTitre, policeNormale, y, "Analyse IA :", texteAnalyse);

            // ----- Lois associées -----
            StringBuilder loisTexte = new StringBuilder();
            List<ArticleLoi> articles = analyse.getArticlesTrouves();
            if (articles == null || articles.isEmpty()) {
                loisTexte.append("Aucun article correspondant trouve.");
            } else {
                for (ArticleLoi a : articles) {
                    loisTexte.append("Article ").append(a.getNumeroArticle()).append(" : ")
                            .append(a.getContenu()).append("\n");
                }
            }
            y = ecrireSection(contenu, policeTitre, policeNormale, y, "Lois associees :", loisTexte.toString());

            // ----- Conclusion -----
            y = ecrireSection(contenu, policeTitre, policeNormale, y, "Conclusion :",
                    "Ce rapport est genere automatiquement par l'assistant IA local. Il constitue une AIDE a la "
                            + "preparation du dossier et ne remplace en aucun cas l'analyse et la decision du juge ou de l'avocat.");

            contenu.close();
            document.save(cheminSortie);
        }
    }

    /**
     * Écrit un titre de section en gras, suivi du texte associé
     * (avec retour à la ligne automatique). Retourne la nouvelle
     * position verticale (y) pour continuer à écrire en dessous.
     */
    private float ecrireSection(PDPageContentStream contenu, PDType1Font policeTitre, PDType1Font policeNormale,
                                 float y, String titre, String texte) throws IOException {
        y -= 20;
        y = ecrireTexte(contenu, policeTitre, 12, MARGE_GAUCHE, y, titre);
        y -= 15;
        y = ecrireTexteMultiligne(contenu, policeNormale, 10, MARGE_GAUCHE, y, texte);
        return y;
    }

    /** Écrit une seule ligne de texte et retourne la position y utilisée (pas de retour à la ligne). */
    private float ecrireTexte(PDPageContentStream contenu, PDType1Font police, int taille,
                               float x, float y, String texte) throws IOException {
        contenu.beginText();
        contenu.setFont(police, taille);
        contenu.newLineAtOffset(x, y);
        contenu.showText(nettoyerPourPDF(texte));
        contenu.endText();
        return y;
    }

    /**
     * Écrit un texte potentiellement long en le découpant automatiquement
     * en plusieurs lignes pour qu'il tienne dans la largeur de la page.
     */
    private float ecrireTexteMultiligne(PDPageContentStream contenu, PDType1Font police, int taille,
                                         float x, float y, String texte) throws IOException {
        String[] paragraphes = texte.split("\n");
        float yCourant = y;

        for (String paragraphe : paragraphes) {
            List<String> lignes = decouperEnLignes(paragraphe, police, taille, LARGEUR_LIGNE);
            for (String ligne : lignes) {
                contenu.beginText();
                contenu.setFont(police, taille);
                contenu.newLineAtOffset(x, yCourant);
                contenu.showText(nettoyerPourPDF(ligne));
                contenu.endText();
                yCourant -= 14;
            }
        }
        return yCourant;
    }

    /** Découpe un texte en plusieurs lignes qui ne dépassent pas la largeur donnée. */
    private List<String> decouperEnLignes(String texte, PDType1Font police, int taille, float largeurMax) throws IOException {
        List<String> lignes = new java.util.ArrayList<>();
        String[] mots = texte.split(" ");
        StringBuilder ligneCourante = new StringBuilder();

        for (String mot : mots) {
            String essai = ligneCourante.isEmpty() ? mot : ligneCourante + " " + mot;
            float largeurTexte = police.getStringWidth(nettoyerPourPDF(essai)) / 1000 * taille;

            if (largeurTexte > largeurMax) {
                lignes.add(ligneCourante.toString());
                ligneCourante = new StringBuilder(mot);
            } else {
                ligneCourante = new StringBuilder(essai);
            }
        }
        if (!ligneCourante.isEmpty()) {
            lignes.add(ligneCourante.toString());
        }
        return lignes;
    }

    /**
     * PDFBox (police standard WinAnsi) ne supporte pas tous les
     * caractères accentués correctement selon les versions ; on
     * remplace donc les accents par leur équivalent simple pour
     * éviter les erreurs de génération.
     */
    private String nettoyerPourPDF(String texte) {
        if (texte == null) return "";
        String t = texte;
        t = t.replace("é", "e").replace("è", "e").replace("ê", "e").replace("ë", "e");
        t = t.replace("à", "a").replace("â", "a").replace("ä", "a");
        t = t.replace("î", "i").replace("ï", "i");
        t = t.replace("ô", "o").replace("ö", "o");
        t = t.replace("ù", "u").replace("û", "u").replace("ü", "u");
        t = t.replace("ç", "c");
        return t;
    }
}
