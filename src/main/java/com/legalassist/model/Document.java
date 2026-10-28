package com.legalassist.model;

import java.time.LocalDate;

/**
 * ============================================================
 * CLASSE Document
 * ============================================================
 * Représente un DOCUMENT (pièce jointe) lié à un dossier,
 * par exemple un contrat, une preuve, une pièce d'identité...
 * (table "document").
 * ============================================================
 */
public class Document {

    private int id;
    private int dossierId;
    private String nomFichier;
    private String cheminFichier;  // chemin sur le disque local
    private String type;           // "PDF", "IMAGE", "TXT"...
    private LocalDate dateAjout;

    public Document() {
    }

    public Document(int id, int dossierId, String nomFichier, String cheminFichier, String type, LocalDate dateAjout) {
        this.id = id;
        this.dossierId = dossierId;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.type = type;
        this.dateAjout = dateAjout;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDossierId() { return dossierId; }
    public void setDossierId(int dossierId) { this.dossierId = dossierId; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }

    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDate dateAjout) { this.dateAjout = dateAjout; }
}
