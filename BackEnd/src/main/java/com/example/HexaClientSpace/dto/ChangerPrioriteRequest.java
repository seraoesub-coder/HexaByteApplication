package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.PrioriteReclamation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangerPrioriteRequest(
        @NotNull(message = "La priorité est obligatoire")
        PrioriteReclamation priorite,

        @Size(max = 200, message = "Le motif ne doit pas dépasser 200 caractères")
        String motif
) {}