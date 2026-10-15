package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Niveau 1 de classification d'une réclamation (WIFI, Forfait, Paiement...).
 */
@Entity
@Table(name = "categories",
       uniqueConstraints = @UniqueConstraint(name = "uk_categories_code", columnNames = "code"))
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code technique stable, utilisé par le front et le prompt IA (ex. WIFI, PAIEMENT). */
    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(length = 255)
    private String description;

    /** Ordre d'affichage dans le formulaire. */
    @Column(nullable = false)
    private int ordre = 0;

    /** Permet de masquer une catégorie sans la supprimer. */
    @Column(nullable = false)
    private boolean actif = true;

    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    @OrderBy("ordre ASC")
    private List<SousCategorie> sousCategories = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public List<SousCategorie> getSousCategories() { return sousCategories; }
    public void setSousCategories(List<SousCategorie> sousCategories) { this.sousCategories = sousCategories; }
}