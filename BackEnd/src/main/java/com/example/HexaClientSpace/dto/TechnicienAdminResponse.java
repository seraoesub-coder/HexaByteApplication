package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Technicien;

/** Vue complète réservée à l'administrateur (avec identifiant et état actif). */
public record TechnicienAdminResponse(
        Long id,
        String nom,
        String prenom,
        String telephone,
        int niveauExperience,
        boolean actif
) {
    public static TechnicienAdminResponse from(Technicien t) {
        return new TechnicienAdminResponse(t.getId(), t.getNom(), t.getPrenom(), t.getTelephone(),
                t.getNiveauExperience(), t.isActif());
    }
}