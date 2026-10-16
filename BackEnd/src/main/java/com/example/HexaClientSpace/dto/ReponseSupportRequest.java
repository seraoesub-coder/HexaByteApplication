package com.example.HexaClientSpace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReponseSupportRequest(
        @NotBlank(message = "Le message est obligatoire")
        @Size(max = 2000, message = "Le message ne doit pas dépasser 2000 caractères")
        String contenu
) {}