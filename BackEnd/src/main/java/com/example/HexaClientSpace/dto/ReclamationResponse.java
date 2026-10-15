package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Reclamation;

import java.time.LocalDateTime;

public record ReclamationResponse(
        Long id,
        String reference,
        String statut,
        String priorite,
        String description,
        String categorieCode,
        String categorieLibelle,
        String sousCategorieCode,
        String sousCategorieLibelle,
        LocalDateTime dateCreation,
        LocalDateTime dateMiseAJour
) {
    /** À appeler dans une transaction (ou après un join fetch) : lit les relations lazy. */
    public static ReclamationResponse from(Reclamation r) {
        var sc = r.getSousCategorie();
        var c = sc.getCategorie();
        return new ReclamationResponse(
                r.getId(), r.getReference(), r.getStatut().name(), r.getPriorite().name(),
                r.getDescription(), c.getCode(), c.getLibelle(), sc.getCode(), sc.getLibelle(),
                r.getDateCreation(), r.getDateMiseAJour());
    }
}