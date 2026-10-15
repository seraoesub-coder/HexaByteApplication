package com.example.HexaClientSpace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * Profil metier Hexabyte : reference abonne, coordonnees, adresse d'installation.
 */
@Entity
@Table(name = "clients",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_clients_numero", columnNames = "numero_client"),
           @UniqueConstraint(name = "uk_clients_user", columnNames = "user_id")
       })
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference abonne Hexabyte, ex. HXB-2026-000142 */
    @Column(name = "numero_client", nullable = false, length = 30)
    private String numeroClient;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String nom;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String prenom;

    /** Format tunisien : 8 chiffres, eventuellement prefixe +216 */
    @Pattern(regexp = "^(\\+216)?[0-9]{8}$", message = "Numero de telephone invalide")
    @Column(length = 20)
    private String telephone;

    @Column(name = "adresse_installation", length = 255)
    private String adresseInstallation;

    @Column(length = 80)
    private String gouvernorat;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /** Cote proprietaire : la FK user_id est portee par cette table. */
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_clients_user"))
    private User user;

    @PrePersist
    void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }

    // ---------- Getters / Setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroClient() { return numeroClient; }
    public void setNumeroClient(String numeroClient) { this.numeroClient = numeroClient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresseInstallation() { return adresseInstallation; }
    public void setAdresseInstallation(String a) { this.adresseInstallation = a; }

    public String getGouvernorat() { return gouvernorat; }
    public void setGouvernorat(String gouvernorat) { this.gouvernorat = gouvernorat; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
