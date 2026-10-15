package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.RendezVous;
import com.example.HexaClientSpace.entity.StatutRendezVous;

import java.time.LocalDateTime;

public record RendezVousResponse(
        Long id,
        String reference,
        String statut,
        LocalDateTime dateHeure,
        int dureeMinutes,
        String adresse,
        String motif,
        String reclamationReference,
        TechnicienResponse technicien,
        LocalDateTime dateCreation,
        LocalDateTime dateConfirmation
) {
    /** À appeler dans une transaction (ou après un join fetch) : lit des relations lazy. */
    public static RendezVousResponse from(RendezVous r) {
        // Le technicien n'est communiqué qu'une fois le rendez-vous confirmé (ou réalisé).
        boolean technicienVisible = r.getStatut() == StatutRendezVous.CONFIRME
                || r.getStatut() == StatutRendezVous.REALISE;
        return new RendezVousResponse(
                r.getId(), r.getReference(), r.getStatut().name(), r.getDateHeure(), r.getDureeMinutes(),
                r.getAdresse(), r.getMotif(),
                r.getReclamation() == null ? null : r.getReclamation().getReference(),
                technicienVisible && r.getTechnicien() != null ? TechnicienResponse.from(r.getTechnicien()) : null,
                r.getDateCreation(), r.getDateConfirmation());
    }
}