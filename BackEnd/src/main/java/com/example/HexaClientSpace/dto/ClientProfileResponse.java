package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Client;

public record ClientProfileResponse(
        Long clientId,
        String numeroClient,
        String nom,
        String prenom,
        String telephone,
        String adresseInstallation,
        String gouvernorat,
        String email
) {
    public static ClientProfileResponse from(Client c) {
        return new ClientProfileResponse(
                c.getId(), c.getNumeroClient(), c.getNom(), c.getPrenom(),
                c.getTelephone(), c.getAdresseInstallation(), c.getGouvernorat(),
                c.getUser().getEmail());
    }
}
