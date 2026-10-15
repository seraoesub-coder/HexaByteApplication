package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Réclamation déposée par un client. La catégorie se déduit de la sous-catégorie
 * (sousCategorie.categorie), ce qui rend toute incohérence catégorie/sous-catégorie impossible.
 */
@Entity
@Table(name = "reclamations",
       uniqueConstraints = @UniqueConstraint(name = "uk_reclamations_reference", columnNames = "reference"),
       indexes = @Index(name = "idx_reclamations_client", columnList = "client_id"))
public class Reclamation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Référence lisible communiquée au client, ex. REC-2026-000001. */
    @Column(nullable = false, length = 40)
    private String reference;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutReclamation statut = StatutReclamation.OUVERTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioriteReclamation priorite = PrioriteReclamation.NORMALE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_mise_a_jour", nullable = false)
    private LocalDateTime dateMiseAJour;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_reclamations_client"))
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sous_categorie_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_reclamations_sous_categorie"))
    private SousCategorie sousCategorie;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.dateCreation = now;
        this.dateMiseAJour = now;
    }

    @PreUpdate
    void preUpdate() {
        this.dateMiseAJour = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatutReclamation getStatut() { return statut; }
    public void setStatut(StatutReclamation statut) { this.statut = statut; }

    public PrioriteReclamation getPriorite() { return priorite; }
    public void setPriorite(PrioriteReclamation priorite) { this.priorite = priorite; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateMiseAJour() { return dateMiseAJour; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public SousCategorie getSousCategorie() { return sousCategorie; }
    public void setSousCategorie(SousCategorie sousCategorie) { this.sousCategorie = sousCategorie; }
}