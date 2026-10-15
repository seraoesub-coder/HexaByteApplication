package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Technicien;

/** niveauExperience : de 1 à 5. */
public record TechnicienResponse(
        String nom,
        String prenom,
        String telephone,
        int niveauExperience
) {
    public static TechnicienResponse from(Technicien t) {
        return new TechnicienResponse(t.getNom(), t.getPrenom(), t.getTelephone(), t.getNiveauExperience());
    }
}