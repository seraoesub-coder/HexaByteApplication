package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.CategorieResponse;
import com.example.HexaClientSpace.repository.CategorieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    /** Arbre complet catégories > sous-catégories, prêt pour le formulaire de réclamation. */
    @Transactional(readOnly = true)
    public List<CategorieResponse> listerActives() {
        return categorieRepository.findAllActivesAvecSousCategories().stream()
                .map(CategorieResponse::from)
                .toList();
    }
}