package com.legalassist.ai;

import com.legalassist.dao.MotCleDAO;
import com.legalassist.model.ArticleLoi;

import java.util.*;

/**
 * ============================================================
 * CLASSE TextClassifier — le "cerveau" de l'IA locale
 * ============================================================
 * OBJECTIF PÉDAGOGIQUE (pour un débutant) :
 * Une vraie IA de type "Machine Learning" (avec Weka ou
 * OpenNLP) demande un entraînement statistique assez complexe.
 * Pour que tu puisses COMPRENDRE, LIRE et FAIRE ÉVOLUER ce
 * code facilement, cette version utilise un algorithme simple
 * mais efficace, appelé "classification par mots-clés
 * pondérés" (bag-of-words + score de similarité).
 *
 * PRINCIPE :
 * 1) On construit un dictionnaire {domaine juridique -> mots-clés}
 *    à partir du fichier training_data.csv (voir TrainingModel).
 * 2) Quand l'utilisateur tape une description, on découpe son
 *    texte en mots ("tokenisation").
 * 3) On compte combien de mots du texte correspondent aux
 *    mots-clés de chaque domaine.
 * 4) Le domaine qui obtient le plus de correspondances est le
 *    résultat de la classification.
 *
 * Ce code peut être remplacé plus tard par un vrai modèle Weka
 * (classe TrainingModel) sans changer le reste de l'application,
 * car les autres classes utilisent seulement les méthodes
 * publiques ci-dessous (classifier, rechercherArticles...).
 * ============================================================
 */
public class TextClassifier {

    /**
     * INSTANCE PARTAGÉE (singleton) : toute l'application utilise le
     * MÊME "cerveau IA" via getInstance(), au lieu que chaque écran
     * crée son propre TextClassifier isolé. Comme ça, quand
     * l'administrateur entraîne l'IA depuis l'écran "Entraînement IA",
     * l'écran "Assistant IA" voit immédiatement les nouveaux mots-clés
     * appris, sans avoir besoin de redémarrer l'application.
     */
    private static TextClassifier instancePartagee;

    /** Retourne toujours la même instance de TextClassifier (singleton). */
    public static TextClassifier getInstance() {
        if (instancePartagee == null) {
            instancePartagee = new TextClassifier();
        }
        return instancePartagee;
    }

    /**
     * Dictionnaire associant chaque CATÉGORIE JURIDIQUE à une liste
     * de mots-clés typiques. C'est notre "base d'apprentissage"
     * simplifiée. Elle peut être enrichie via l'écran
     * "Entraînement IA" (voir TrainingModel + training_data.csv).
     */
    private final Map<String, List<String>> dictionnaireCategories = new LinkedHashMap<>();

    public TextClassifier() {
        initialiserDictionnaireParDefaut();
        chargerMotsClesPersistants();
    }

    /**
     * Recharge dans le dictionnaire tous les mots-clés qui ont été
     * enregistrés en base par un entraînement précédent (table
     * mot_cle_ia). Appelée une seule fois, à la création de
     * l'instance partagée, pour que l'IA "se souvienne" de ce
     * qu'elle a appris entre deux lancements de l'application.
     *
     * Si la base de données n'est pas encore accessible (ex : au
     * tout début du démarrage), on ignore simplement l'erreur pour
     * ne pas bloquer l'application — l'IA fonctionnera avec son
     * dictionnaire de base uniquement, comme avant.
     */
    private void chargerMotsClesPersistants() {
        try {
            MotCleDAO motCleDAO = new MotCleDAO();
            Map<String, List<String>> motsClesParCategorie = motCleDAO.listerTousParCategorie();

            for (Map.Entry<String, List<String>> entree : motsClesParCategorie.entrySet()) {
                dictionnaireCategories
                        .computeIfAbsent(entree.getKey(), k -> new ArrayList<>())
                        .addAll(entree.getValue());
            }
        } catch (Exception e) {
            System.err.println("Impossible de charger l'entraînement précédent de l'IA : " + e.getMessage());
        }
    }

    /**
     * Remplit le dictionnaire avec des catégories de base.
     * Ces données peuvent aussi être chargées dynamiquement
     * depuis training_data.csv grâce à TrainingModel.chargerDataset().
     */
    private void initialiserDictionnaireParDefaut() {
        dictionnaireCategories.put("Droit foncier", new ArrayList<>(Arrays.asList(
                "terrain", "propriete", "propriété", "cadastre", "titre foncier",
                "borne", "litige", "occupation", "parcelle", "possession")));

        dictionnaireCategories.put("Droit pénal", new ArrayList<>(Arrays.asList(
                "vol", "agression", "coups", "blessures", "meurtre", "escroquerie",
                "plainte", "delit", "délit", "crime", "prison", "police")));

        dictionnaireCategories.put("Droit du travail", new ArrayList<>(Arrays.asList(
                "licenciement", "contrat de travail", "employeur", "salarie", "salarié",
                "salaire", "conge", "congé", "demission", "démission", "preavis", "préavis")));

        dictionnaireCategories.put("Droit de la famille", new ArrayList<>(Arrays.asList(
                "divorce", "mariage", "garde", "enfant", "pension alimentaire",
                "succession", "heritage", "héritage", "adoption")));

        dictionnaireCategories.put("Droit administratif", new ArrayList<>(Arrays.asList(
                "administration", "fonctionnaire", "permis", "commune", "mairie",
                "recours", "decision administrative", "décision administrative")));

        dictionnaireCategories.put("Procédure judiciaire", new ArrayList<>(Arrays.asList(
                "tribunal", "audience", "juge", "greffe", "convocation", "assignation",
                "appel", "jugement", "avocat")));
    }

    /**
     * Permet à l'écran "Entraînement IA" d'ajouter/mettre à jour
     * les mots-clés d'une catégorie à partir du fichier CSV chargé
     * par l'administrateur (voir TrainingModel).
     *
     * Retourne la liste des mots-clés qui viennent d'être ajoutés,
     * pour que l'appelant (EntrainementIAController, TrainingModel)
     * puisse les enregistrer de façon PERMANENTE via MotCleDAO —
     * sinon cet apprentissage resterait uniquement en mémoire et
     * disparaîtrait à la fermeture de l'application.
     */
    public List<String> ajouterExempleEntrainement(String texte, String categorie) {
        List<String> mots = tokeniser(texte);
        dictionnaireCategories
                .computeIfAbsent(categorie, k -> new ArrayList<>())
                .addAll(mots);
        return mots;
    }

    /**
     * Retourne la liste des catégories juridiques actuellement connues
     * par l'IA, avec le nombre de mots-clés appris pour chacune.
     * Utilisé par l'écran "Entraînement IA" pour afficher l'état actuel.
     */
    public Map<String, Integer> getStatistiquesParCategorie() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entree : dictionnaireCategories.entrySet()) {
            stats.put(entree.getKey(), entree.getValue().size());
        }
        return stats;
    }

    /**
     * Classifie un texte libre et retourne la catégorie juridique la plus
     * probable, avec un score de confiance simple (nombre de mots
     * correspondants / nombre de mots du texte).
     */
    public Prediction classifier(String texteDescription) {
        List<String> motsTexte = tokeniser(texteDescription);

        String meilleureCategorie = "Non déterminé";
        int meilleurScore = 0;

        for (Map.Entry<String, List<String>> entree : dictionnaireCategories.entrySet()) {
            String categorie = entree.getKey();
            List<String> motsClesCategorie = entree.getValue();

            int score = compterCorrespondances(motsTexte, motsClesCategorie);

            if (score > meilleurScore) {
                meilleurScore = score;
                meilleureCategorie = categorie;
            }
        }

        double confiance = motsTexte.isEmpty() ? 0.0 : Math.min(1.0, (double) meilleurScore / 3.0);

        return new Prediction(meilleureCategorie, confiance);
    }

    /**
     * Recherche, parmi une liste d'articles de loi (venant de la base
     * de données), ceux dont les mots-clés correspondent le mieux au
     * texte fourni. Utilisé par RechercheJuridiqueService.
     *
     * @param texte    texte libre tapé par l'utilisateur
     * @param articles liste de TOUS les articles disponibles (chargés depuis la BD)
     * @return liste triée des articles les plus pertinents en premier
     */
    public List<ArticleLoi> rechercherArticlesPertinents(String texte, List<ArticleLoi> articles) {
        List<String> motsTexte = tokeniser(texte);

        // On calcule un score pour chaque article, puis on trie du plus pertinent au moins pertinent
        List<ArticleLoi> resultatsTries = new ArrayList<>(articles);

        resultatsTries.sort((a1, a2) -> {
            int score1 = compterCorrespondances(motsTexte, splitMotsCles(a1.getMotsCles()));
            int score2 = compterCorrespondances(motsTexte, splitMotsCles(a2.getMotsCles()));
            return Integer.compare(score2, score1); // ordre décroissant
        });

        // On ne garde que les articles qui ont au moins 1 correspondance
        List<ArticleLoi> resultatsFinal = new ArrayList<>();
        for (ArticleLoi article : resultatsTries) {
            int score = compterCorrespondances(motsTexte, splitMotsCles(article.getMotsCles()));
            if (score > 0) {
                resultatsFinal.add(article);
            }
        }
        return resultatsFinal;
    }

    /**
     * Compte combien de mots-clés apparaissent dans le texte tokenisé.
     * Un mot-clé peut être composé de plusieurs mots (ex: "titre foncier"),
     * donc on vérifie s'il apparaît comme "sous-chaîne" du texte original.
     */
    private int compterCorrespondances(List<String> motsTexte, List<String> motsCles) {
        String texteJoint = " " + String.join(" ", motsTexte) + " ";
        int score = 0;
        for (String motCle : motsCles) {
            String motCleNormalise = normaliser(motCle);
            if (texteJoint.contains(" " + motCleNormalise + " ") || texteJoint.contains(motCleNormalise)) {
                score++;
            }
        }
        return score;
    }

    /**
     * Découpe un texte en mots simples, en minuscule, sans ponctuation.
     * Exemple : "Mon client possède un terrain !" -> [mon, client, possede, un, terrain]
     */
    private List<String> tokeniser(String texte) {
        if (texte == null || texte.isBlank()) {
            return new ArrayList<>();
        }
        String nettoye = normaliser(texte);
        String[] mots = nettoye.split("\\s+");
        return new ArrayList<>(Arrays.asList(mots));
    }

    /**
     * Sépare une chaîne "mot1,mot2,mot3" (mots-clés stockés en base)
     * en liste de mots-clés individuels.
     */
    private List<String> splitMotsCles(String motsCles) {
        if (motsCles == null || motsCles.isBlank()) {
            return new ArrayList<>();
        }
        String[] parts = motsCles.split(",");
        List<String> liste = new ArrayList<>();
        for (String p : parts) {
            liste.add(p.trim());
        }
        return liste;
    }

    /**
     * Met un texte en minuscule et retire les accents + la ponctuation,
     * pour rendre la comparaison de mots plus fiable.
     */
    private String normaliser(String texte) {
        String t = texte.toLowerCase();
        t = t.replaceAll("[àâä]", "a");
        t = t.replaceAll("[éèêë]", "e");
        t = t.replaceAll("[îï]", "i");
        t = t.replaceAll("[ôö]", "o");
        t = t.replaceAll("[ùûü]", "u");
        t = t.replaceAll("ç", "c");
        t = t.replaceAll("[^a-z0-9\\s]", " "); // supprime la ponctuation
        return t.trim();
    }
}
