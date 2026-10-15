package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Note (1 à 5) donnée par le client à UNE réponse précise de l'agent.
 * Une seule évaluation par message (contrainte d'unicité).
 */
@Entity
@Table(name = "evaluations",
       uniqueConstraints = @UniqueConstraint(name = "uk_evaluations_message", columnNames = "message_id"))
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_evaluations_message"))
    private MessageIA message;

    @Column(nullable = false)
    private int note;

    @Column(length = 500)
    private String commentaire;

    @Column(name = "date_evaluation", nullable = false, updatable = false)
    private LocalDateTime dateEvaluation;

    @PrePersist
    void prePersist() {
        this.dateEvaluation = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public MessageIA getMessage() { return message; }
    public void setMessage(MessageIA message) { this.message = message; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getDateEvaluation() { return dateEvaluation; }
}