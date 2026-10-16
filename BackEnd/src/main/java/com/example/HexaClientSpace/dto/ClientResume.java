package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Client;

/** Informations client visibles par le support uniquement. */
public record ClientResume(
        Long clientId,
        String numeroClient,
        String nom,
        String prenom,
        String telephone,
        String email,
        String adresseInstallation
) {
    /** À appeler dans une transaction : lit l'utilisateur lié (relation lazy). */
    public static ClientResume from(Client c) {
        return new ClientResume(c.getId(), c.getNumeroClient(), c.getNom(), c.getPrenom(),
                c.getTelephone(), c.getUser().getEmail(), c.getAdresseInstallation());
    }
}