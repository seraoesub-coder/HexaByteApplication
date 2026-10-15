package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.HistoriqueStatut;

import java.time.LocalDateTime;

public record HistoriqueStatutResponse(
        String ancienStatut,
        String nouveauStatut,
        String acteur,
        String motif,
        LocalDateTime dateChangement
) {
    public static HistoriqueStatutResponse from(HistoriqueStatut h) {
        return new HistoriqueStatutResponse(
                h.getAncienStatut() == null ? null : h.getAncienStatut().name(),
                h.getNouveauStatut().name(),
                h.getActeur().name(),
                h.getMotif(),
                h.getDateChangement());
    }
}