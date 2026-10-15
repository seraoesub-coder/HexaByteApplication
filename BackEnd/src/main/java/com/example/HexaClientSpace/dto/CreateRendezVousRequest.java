package com.example.HexaClientSpace.dto;

import jakarta.validation.constraints.Size;

/** Corps facultatif : lier la demande à une réclamation et/ou préciser le motif. */
public record CreateRendezVousRequest(
        Long reclamationId,

        @Size(max = 255, message = "Le motif ne doit pas dépasser 255 caractères")
        String motif
) {}