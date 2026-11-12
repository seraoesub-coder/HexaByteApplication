package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.TechnicienAdminResponse;
import com.example.HexaClientSpace.dto.TechnicienRequest;
import com.example.HexaClientSpace.services.TechnicienService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Gestion des techniciens : réservée à l'administrateur (principe du moindre privilège). */
@RestController
@RequestMapping("/api/admin/techniciens")
@PreAuthorize("hasRole('ADMIN')")
public class TechnicienAdminController {

    private final TechnicienService technicienService;

    public TechnicienAdminController(TechnicienService technicienService) {
        this.technicienService = technicienService;
    }

    /** Liste paginée, triée par nom. Filtre facultatif : ?actif=true ou ?actif=false */
    @GetMapping
    public Page<TechnicienAdminResponse> lister(
            @RequestParam(required = false) Boolean actif,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return technicienService.lister(actif, pageable);
    }

    @GetMapping("/{id}")
    public TechnicienAdminResponse obtenir(@PathVariable Long id) {
        return technicienService.obtenir(id);
    }

    @PostMapping
    public ResponseEntity<TechnicienAdminResponse> creer(@Valid @RequestBody TechnicienRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(technicienService.creer(request));
    }

    @PutMapping("/{id}")
    public TechnicienAdminResponse modifier(@PathVariable Long id,
                                            @Valid @RequestBody TechnicienRequest request) {
        return technicienService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        technicienService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}