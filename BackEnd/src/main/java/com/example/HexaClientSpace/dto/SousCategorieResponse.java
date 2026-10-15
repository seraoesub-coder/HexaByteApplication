package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.SousCategorie;

public record SousCategorieResponse(Long id, String code, String libelle) {

    public static SousCategorieResponse from(SousCategorie s) {
        return new SousCategorieResponse(s.getId(), s.getCode(), s.getLibelle());
    }
}