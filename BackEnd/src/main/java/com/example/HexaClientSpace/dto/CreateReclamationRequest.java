package com.example.HexaClientSpace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReclamationRequest(

        @NotNull(message = "La sous-catégorie est obligatoire")
        Long sousCategorieId,

        @NotBlank(message = "La description est obligatoire")
        @Size(min = 10, max = 2000, message = "La description doit contenir entre 10 et 2000 caractères")
        String description
) {}