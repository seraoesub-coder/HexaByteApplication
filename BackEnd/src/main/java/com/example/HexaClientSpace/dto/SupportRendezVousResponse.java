package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.RendezVous;

public record SupportRendezVousResponse(
        RendezVousResponse rendezVous,
        ClientResume client
) {
    public static SupportRendezVousResponse from(RendezVous r) {
        return new SupportRendezVousResponse(RendezVousResponse.from(r), ClientResume.from(r.getClient()));
    }
}