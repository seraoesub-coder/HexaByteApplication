package com.example.HexaClientSpace.dto;


import jakarta.validation.constraints.*;

/**
 * Payload d'inscription : cree en une seule transaction le compte (User)
 * et le profil abonne (Client).
 */
public record RegisterRequest(

        @NotBlank @Email
        String email,

        @NotBlank
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caracteres")
        @Pattern(regexp = ".*[A-Z].*", message = "Au moins une majuscule est requise")
        @Pattern(regexp = ".*[0-9].*", message = "Au moins un chiffre est requis")
        String motDePasse,

        @NotBlank String nom,

        @NotBlank String prenom,

        @Pattern(regexp = "^(\\+216)?[0-9]{8}$", message = "Numero de telephone invalide")
        String telephone,

        String adresseInstallation,

        String gouvernorat
) {}
