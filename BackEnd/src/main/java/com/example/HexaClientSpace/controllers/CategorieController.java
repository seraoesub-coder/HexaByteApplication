package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.CategorieResponse;
import com.example.HexaClientSpace.services.CategorieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    /** Public (déjà autorisé dans SecurityConfig) : alimente le formulaire de réclamation. */
    @GetMapping
    public ResponseEntity<List<CategorieResponse>> lister() {
        return ResponseEntity.ok(categorieService.listerActives());
    }
}