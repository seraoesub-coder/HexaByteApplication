package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Intervention à domicile. Le créneau est proposé par le système ; le technicien n'est
 * affecté (au hasard parmi les disponibles) qu'à la confirmation par le client.
 */
@Entity
@Table(name = "rendez_vous",
       uniqueConstraints = @UniqueConstraint(name = "uk_rendez_vous_reference", columnNames = "reference"),
       indexes = {
           @Index(name = "idx_rdv_client", columnList = "client_id"),
           @Index(name = "idx_rdv_creneau", columnList = "date_heure, statut")
       })
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Référence lisible, ex. RDV-2026-000001. */
    @Column(nullable = false, length = 40)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_rdv_client"))
    private Client client;

    /** Facultatif : réclamation à l'origine de la demande. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamation_id", foreignKey = @ForeignKey(name = "fk_rdv_reclamation"))
    private Reclamation reclamation;

    /** Renseigné uniquement à la confirmation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id", foreignKey = @ForeignKey(name = "fk_rdv_technicien"))
    private Technicien technicien;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "duree_minutes", nullable = false)
    private int dureeMinutes;

    /** Copie de l'adresse d'installation du client au moment de la demande. */
    @Column(nullable = false, length = 255)
    private String adresse;

    @Column(length = 255)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatutRendezVous statut = StatutRendezVous.PROPOSE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_confirmation")
    private LocalDateTime dateConfirmation;

    @PrePersist
    void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Reclamation getReclamation() { return reclamation; }
    public void setReclamation(Reclamation reclamation) { this.reclamation = reclamation; }

    public Technicien getTechnicien() { return technicien; }
    public void setTechnicien(Technicien technicien) { this.technicien = technicien; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public StatutRendezVous getStatut() { return statut; }
    public void setStatut(StatutRendezVous statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public LocalDateTime getDateConfirmation() { return dateConfirmation; }
    public void setDateConfirmation(LocalDateTime dateConfirmation) { this.dateConfirmation = dateConfirmation; }
}