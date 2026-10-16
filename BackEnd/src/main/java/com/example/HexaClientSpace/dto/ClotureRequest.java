package com.example.HexaClientSpace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Le motif est obligatoire : il est conservé dans l'historique des statuts. */
public record ClotureRequest(
        @NotBlank(message = "Le motif de clôture est obligatoire")
        @Size(max = 230, message = "Le motif ne doit pas dépasser 230 caractères")
        String motif
) {}