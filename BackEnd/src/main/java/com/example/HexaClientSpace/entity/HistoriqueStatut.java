package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Journal de chaque changement de statut d'une réclamation (traçabilité). */
@Entity
@Table(name = "historique_statuts",
       indexes = @Index(name = "idx_historique_reclamation", columnList = "reclamation_id"))
public class HistoriqueStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reclamation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_historique_reclamation"))
    private Reclamation reclamation;

    /** null pour la ligne de création. */
    @Enumerated(EnumType.STRING)
    @Column(name = "ancien_statut", length = 30)
    private StatutReclamation ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouveau_statut", nullable = false, length = 30)
    private StatutReclamation nouveauStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ActeurStatut acteur;

    @Column(length = 255)
    private String motif;

    @Column(name = "date_changement", nullable = false, updatable = false)
    private LocalDateTime dateChangement;

    @PrePersist
    void prePersist() {
        this.dateChangement = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Reclamation getReclamation() { return reclamation; }
    public void setReclamation(Reclamation reclamation) { this.reclamation = reclamation; }

    public StatutReclamation getAncienStatut() { return ancienStatut; }
    public void setAncienStatut(StatutReclamation ancienStatut) { this.ancienStatut = ancienStatut; }

    public StatutReclamation getNouveauStatut() { return nouveauStatut; }
    public void setNouveauStatut(StatutReclamation nouveauStatut) { this.nouveauStatut = nouveauStatut; }

    public ActeurStatut getActeur() { return acteur; }
    public void setActeur(ActeurStatut acteur) { this.acteur = acteur; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public LocalDateTime getDateChangement() { return dateChangement; }
}