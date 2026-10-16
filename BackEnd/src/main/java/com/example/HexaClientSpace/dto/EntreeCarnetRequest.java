package com.example.HexaClientSpace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntreeCarnetRequest(
        @NotBlank(message = "L'entrée du carnet est obligatoire")
        @Size(max = 2000, message = "L'entrée ne doit pas dépasser 2000 caractères")
        String contenu
) {}