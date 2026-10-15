package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

/**
 * Niveau 2 : précise le problème au sein d'une catégorie
 * (ex. WIFI > "Signal WIFI faible ou instable").
 */
@Entity
@Table(name = "sous_categories",
       uniqueConstraints = @UniqueConstraint(name = "uk_sous_categories_code", columnNames = "code"))
public class SousCategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code unique sur l'ensemble des sous-catégories (ex. WIFI_SIGNAL_FAIBLE). */
    @Column(nullable = false, length = 60)
    private String code;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false)
    private int ordre = 0;

    @Column(nullable = false)
    private boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sous_categories_categorie"))
    private Categorie categorie;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
}