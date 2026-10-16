package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Une entrée du carnet de suivi d'une réclamation : journal INTERNE tenu par le personnel.
 * Il mélange les notes écrites par les agents et les traces automatiques (ex. changement de priorité).
 * Jamais exposé au client : aucun DTO côté client ne le contient et sa seule route de lecture
 * est sous /api/support/**.
 */
@Entity
@Table(name = "carnet_entrees",
       indexes = @Index(name = "idx_carnet_reclamation", columnList = "reclamation_id"))
public class EntreeCarnet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reclamation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_carnet_reclamation"))
    private Reclamation reclamation;

    /** Agent à l'origine de l'entrée (lien réel vers le compte, pas un texte libre). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_carnet_auteur"))
    private User auteur;

    @Column(nullable = false, length = 2000)
    private String contenu;

    /** true = trace générée par le système suite à une action de l'agent ; false = note écrite par l'agent. */
    @Column(nullable = false)
    private boolean automatique;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Reclamation getReclamation() { return reclamation; }
    public void setReclamation(Reclamation reclamation) { this.reclamation = reclamation; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User auteur) { this.auteur = auteur; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public boolean isAutomatique() { return automatique; }
    public void setAutomatique(boolean automatique) { this.automatique = automatique; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}