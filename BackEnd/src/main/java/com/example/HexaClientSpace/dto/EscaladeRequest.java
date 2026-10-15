package com.example.HexaClientSpace.dto;

import jakarta.validation.constraints.Size;

/** Corps facultatif de la demande de conseiller humain. */
public record EscaladeRequest(
        @Size(max = 255, message = "Le motif ne doit pas dépasser 255 caractères")
        String motif
) {}