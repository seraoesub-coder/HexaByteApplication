package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Categorie;
import com.example.HexaClientSpace.entity.SousCategorie;

import java.util.Comparator;
import java.util.List;

public record CategorieResponse(
        Long id,
        String code,
        String libelle,
        String description,
        List<SousCategorieResponse> sousCategories
) {
    /** Ne garde que les sous-catégories actives, triées par ordre d'affichage. */
    public static CategorieResponse from(Categorie c) {
        List<SousCategorieResponse> subs = c.getSousCategories().stream()
                .filter(SousCategorie::isActif)
                .sorted(Comparator.comparingInt(SousCategorie::getOrdre))
                .map(SousCategorieResponse::from)
                .toList();
        return new CategorieResponse(c.getId(), c.getCode(), c.getLibelle(), c.getDescription(), subs);
    }
}