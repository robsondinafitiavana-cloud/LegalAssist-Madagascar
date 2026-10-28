package com.legalassist.model;

import java.time.LocalDate;

/**
 * ============================================================
 * CLASSE Dossier
 * ============================================================
 * Représente un DOSSIER JURIDIQUE (une affaire) géré par un
 * avocat. Correspond à la table "dossier" en base de données.
 * ============================================================
 */
public class Dossier {

    private int id;
    private String numero;          // numéro unique du dossier (ex: DOS-2026-001)
    private String titre;
    private String description;     // description libre de l'affaire (utilisée par l'IA)
    private String categorie;       // domaine juridique trouvé par l'IA (ex: "Droit foncier")
    private LocalDate dateCreation;
    private String statut;          // "EN_COURS", "CLOTURE", "ARCHIVE"
    private int avocatResponsableId; // id de l'utilisateur (avocat) responsable

    public Dossier() {
    }

    public Dossier(int id, String numero, String titre, String description,
                    String categorie, LocalDate dateCreation, String statut, int avocatResponsableId) {
        this.id = id;
        this.numero = numero;
        this.titre = titre;
        this.description = description;
        this.categorie = categorie;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.avocatResponsableId = avocatResponsableId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getAvocatResponsableId() { return avocatResponsableId; }
    public void setAvocatResponsableId(int avocatResponsableId) { this.avocatResponsableId = avocatResponsableId; }

    @Override
    public String toString() {
        return numero + " - " + titre;
    }
}
