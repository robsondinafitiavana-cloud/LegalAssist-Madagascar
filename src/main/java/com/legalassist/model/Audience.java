package com.legalassist.model;

import java.time.LocalDate;

/**
 * ============================================================
 * CLASSE Audience
 * ============================================================
 * Représente une AUDIENCE liée à un dossier (table "audience").
 * ============================================================
 */
public class Audience {

    private int id;
    private int dossierId;
    private LocalDate dateAudience;
    private String lieu;
    private String demandeur;
    private String defendeur;
    private String notes;

    public Audience() {
    }

    public Audience(int id, int dossierId, LocalDate dateAudience, String lieu,
                     String demandeur, String defendeur, String notes) {
        this.id = id;
        this.dossierId = dossierId;
        this.dateAudience = dateAudience;
        this.lieu = lieu;
        this.demandeur = demandeur;
        this.defendeur = defendeur;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDossierId() { return dossierId; }
    public void setDossierId(int dossierId) { this.dossierId = dossierId; }

    public LocalDate getDateAudience() { return dateAudience; }
    public void setDateAudience(LocalDate dateAudience) { this.dateAudience = dateAudience; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getDemandeur() { return demandeur; }
    public void setDemandeur(String demandeur) { this.demandeur = demandeur; }

    public String getDefendeur() { return defendeur; }
    public void setDefendeur(String defendeur) { this.defendeur = defendeur; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
