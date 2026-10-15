package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Fil d'échange entre un client et l'agent IA, rattaché à une seule réclamation. */
@Entity
@Table(name = "conversations_ia",
       uniqueConstraints = @UniqueConstraint(name = "uk_conversations_reclamation", columnNames = "reclamation_id"))
public class ConversationIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reclamation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_conversations_reclamation"))
    private Reclamation reclamation;

    @Column(name = "date_debut", nullable = false, updatable = false)
    private LocalDateTime dateDebut;

    @PrePersist
    void prePersist() {
        this.dateDebut = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Reclamation getReclamation() { return reclamation; }
    public void setReclamation(Reclamation reclamation) { this.reclamation = reclamation; }

    public LocalDateTime getDateDebut() { return dateDebut; }
}