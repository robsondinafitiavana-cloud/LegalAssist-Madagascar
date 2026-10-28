package com.legalassist.model;

import java.time.LocalDate;

/**
 * ============================================================
 * CLASSE Loi
 * ============================================================
 * Représente une LOI malagasy enregistrée dans la base
 * juridique locale (table "loi").
 * ============================================================
 */
public class Loi {

    private int id;
    private String nom;
    private String domaine;          // ex: "Droit civil", "Droit pénal"...
    private LocalDate datePublication;
    private String source;

    public Loi() {
    }

    public Loi(int id, String nom, String domaine, LocalDate datePublication, String source) {
        this.id = id;
        this.nom = nom;
        this.domaine = domaine;
        this.datePublication = datePublication;
        this.source = source;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDomaine() { return domaine; }
    public void setDomaine(String domaine) { this.domaine = domaine; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    @Override
    public String toString() {
        return nom + " (" + domaine + ")";
    }
}
