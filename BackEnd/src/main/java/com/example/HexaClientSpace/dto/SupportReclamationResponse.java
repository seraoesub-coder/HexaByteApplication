package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Reclamation;

public record SupportReclamationResponse(
        ReclamationResponse reclamation,
        ClientResume client
) {
    public static SupportReclamationResponse from(Reclamation r) {
        return new SupportReclamationResponse(ReclamationResponse.from(r), ClientResume.from(r.getClient()));
    }
}