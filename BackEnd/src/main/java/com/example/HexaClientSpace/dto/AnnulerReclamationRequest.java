package com.example.HexaClientSpace.dto;


import jakarta.validation.constraints.Size;

public record AnnulerReclamationRequest(
        @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères.")
        String motif
) {}