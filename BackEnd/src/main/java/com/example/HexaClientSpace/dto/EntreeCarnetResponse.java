package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.EntreeCarnet;

import java.time.LocalDateTime;

public record EntreeCarnetResponse(
        Long id,
        Long auteurId,
        String auteurEmail,
        String contenu,
        boolean automatique,
        LocalDateTime dateCreation
) {
    /** À appeler dans une transaction : lit l'auteur (relation lazy). */
    public static EntreeCarnetResponse from(EntreeCarnet e) {
        return new EntreeCarnetResponse(e.getId(), e.getAuteur().getId(), e.getAuteur().getEmail(),
                e.getContenu(), e.isAutomatique(), e.getDateCreation());
    }
}