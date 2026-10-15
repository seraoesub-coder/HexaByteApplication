package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

/** Agent technique pouvant intervenir chez un client (alimenté par data.sql). */
@Entity
@Table(name = "techniciens",
       uniqueConstraints = @UniqueConstraint(name = "uk_techniciens_telephone", columnNames = "telephone"))
public class Technicien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(nullable = false, length = 80)
    private String prenom;

    @Column(nullable = false, length = 20)
    private String telephone;

    /** Niveau d'expérience de 1 à 5. */
    @Column(name = "niveau_experience", nullable = false)
    private int niveauExperience;

    /** false = ne reçoit plus d'affectations (congé, départ...). */
    @Column(nullable = false)
    private boolean actif = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public int getNiveauExperience() { return niveauExperience; }
    public void setNiveauExperience(int niveauExperience) { this.niveauExperience = niveauExperience; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}