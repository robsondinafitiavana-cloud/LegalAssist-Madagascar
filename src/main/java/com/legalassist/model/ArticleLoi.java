package com.legalassist.model;

/**
 * ============================================================
 * CLASSE ArticleLoi
 * ============================================================
 * Représente UN ARTICLE d'une loi (table "article_loi").
 * Une loi contient plusieurs articles.
 * Le champ "motsCles" est très important : c'est lui qui
 * permet à notre IA locale de retrouver l'article pertinent
 * à partir d'un texte libre tapé par l'utilisateur.
 * ============================================================
 */
public class ArticleLoi {

    private int id;
    private int loiId;         // référence vers la loi parente
    private String numeroArticle;
    private String contenu;
    private String motsCles;   // mots-clés séparés par des virgules, ex: "terrain,propriété,litige"

    public ArticleLoi() {
    }

    public ArticleLoi(int id, int loiId, String numeroArticle, String contenu, String motsCles) {
        this.id = id;
        this.loiId = loiId;
        this.numeroArticle = numeroArticle;
        this.contenu = contenu;
        this.motsCles = motsCles;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLoiId() { return loiId; }
    public void setLoiId(int loiId) { this.loiId = loiId; }

    public String getNumeroArticle() { return numeroArticle; }
    public void setNumeroArticle(String numeroArticle) { this.numeroArticle = numeroArticle; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getMotsCles() { return motsCles; }
    public void setMotsCles(String motsCles) { this.motsCles = motsCles; }

    @Override
    public String toString() {
        return "Article " + numeroArticle + " : " + contenu;
    }
}
