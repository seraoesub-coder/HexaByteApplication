package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.RendezVous;
import com.example.HexaClientSpace.entity.StatutRendezVous;
import java.math.BigDecimal;

import java.time.LocalDateTime;

public record RendezVousResponse(
        Long id,
        String reference,
        String statut,
        LocalDateTime dateHeure,
        int dureeMinutes,
        String adresse,
        String motif,
        BigDecimal prix,
        Long clientId,
        String numeroClient,
        String reclamationReference,
        TechnicienResponse technicien,
        LocalDateTime dateCreation,
        LocalDateTime dateConfirmation
) {
    /** À appeler dans une transaction (ou après un join fetch) : lit des relations lazy[cite: 1]. */
    public static RendezVousResponse from(RendezVous r) {
        boolean technicienVisible = r.getStatut() == StatutRendezVous.CONFIRME
                || r.getStatut() == StatutRendezVous.REALISE;
        Long clientId = r.getClient() == null ? null : r.getClient().getId();
        String numeroClient = r.getClient() == null ? null : r.getClient().getNumeroClient();

        return new RendezVousResponse(
                r.getId(),
                r.getReference(),
                r.getStatut().name(),
                r.getDateHeure(),
                r.getDureeMinutes(),
                r.getAdresse(),
                r.getMotif(),
                r.getPrix(),
                clientId,
                numeroClient,
                r.getReclamation() == null ? null : r.getReclamation().getReference(),
                technicienVisible && r.getTechnicien() != null ? TechnicienResponse.from(r.getTechnicien()) : null,
                r.getDateCreation(),
                r.getDateConfirmation()
        );
    }
}