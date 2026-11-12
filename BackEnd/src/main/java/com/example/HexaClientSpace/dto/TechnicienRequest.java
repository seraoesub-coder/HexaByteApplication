package com.example.HexaClientSpace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Corps de création (POST) et de modification complète (PUT) d'un technicien. */
public record TechnicienRequest(

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 80, message = "Le nom ne doit pas dépasser 80 caractères")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 80, message = "Le prénom ne doit pas dépasser 80 caractères")
        String prenom,

        @NotBlank(message = "Le téléphone est obligatoire")
        @Pattern(regexp = "^(\\+216)?[0-9]{8}$",
                 message = "Numéro de téléphone invalide (8 chiffres, avec ou sans +216)")
        String telephone,

        @NotNull(message = "Le niveau d'expérience est obligatoire")
        @Min(value = 1, message = "Le niveau d'expérience minimal est 1")
        @Max(value = 5, message = "Le niveau d'expérience maximal est 5")
        Integer niveauExperience,

        /** Facultatif : absent = actif à la création, inchangé à la modification. */
        Boolean actif
) {}