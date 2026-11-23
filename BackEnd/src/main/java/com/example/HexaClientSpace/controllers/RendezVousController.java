package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.CreateRendezVousRequest;
import com.example.HexaClientSpace.dto.RendezVousResponse;
import com.example.HexaClientSpace.security.CurrentUser;
import com.example.HexaClientSpace.services.RendezVousService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rendez-vous")
@PreAuthorize("hasAnyRole('CLIENT', 'AGENT_SUPPORT', 'ADMIN')")

public class RendezVousController {

    private final RendezVousService rendezVousService;
    private final CurrentUser currentUser;

    public RendezVousController(RendezVousService rendezVousService, CurrentUser currentUser) {
        this.rendezVousService = rendezVousService;
        this.currentUser = currentUser;
    }

    /** Demande un rendez-vous : le système propose une date. Le corps est facultatif. */
    @PostMapping
    public ResponseEntity<RendezVousResponse> demander(
            @Valid @RequestBody(required = false) CreateRendezVousRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rendezVousService.demander(currentUser.getId(), request));
    }

    /** Confirme la date proposée : le technicien affecté est renvoyé dans la réponse. */
    @PostMapping("/{id}/confirmation")
    public RendezVousResponse confirmer(@PathVariable Long id) {
        return rendezVousService.confirmer(currentUser.getId(), id);
    }

    @PostMapping("/{id}/annulation")
    public RendezVousResponse annuler(@PathVariable Long id) {
        return rendezVousService.annuler(currentUser.getId(), id);
    }
/*
    @GetMapping
    public Page<RendezVousResponse> mesRendezVous(
            @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {
        return rendezVousService.lister(currentUser.getId(), pageable);
    }
 */

    
    @GetMapping("/{id}")
    public RendezVousResponse detail(@PathVariable Long id) {
        return rendezVousService.obtenir(currentUser.getId(), id);
    }

    @GetMapping
    public Page<RendezVousResponse> mesRendezVous(
            @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {
            return rendezVousService.listerPourUtilisateur(currentUser.getId(), pageable);
    }    

    @PostMapping("/{id}/realisation")
    @PreAuthorize("hasAnyRole('AGENT_SUPPORT', 'ADMIN')")
    public RendezVousResponse realiser(@PathVariable Long id) {
        return rendezVousService.realiser(currentUser.getId(), id);
    }
}