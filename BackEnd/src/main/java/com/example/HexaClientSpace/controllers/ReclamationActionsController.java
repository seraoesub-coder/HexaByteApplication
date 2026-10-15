package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.EscaladeRequest;
import com.example.HexaClientSpace.dto.HistoriqueStatutResponse;
import com.example.HexaClientSpace.dto.ReclamationResponse;
import com.example.HexaClientSpace.security.CurrentUser;
import com.example.HexaClientSpace.services.ReclamationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations/{reclamationId}")
@PreAuthorize("hasRole('CLIENT')")
public class ReclamationActionsController {

    private final ReclamationService reclamationService;
    private final CurrentUser currentUser;

    public ReclamationActionsController(ReclamationService reclamationService, CurrentUser currentUser) {
        this.reclamationService = reclamationService;
        this.currentUser = currentUser;
    }

    /** Demande de conseiller humain. Le corps {"motif": "..."} est facultatif. */
    @PostMapping("/escalade")
    public ReclamationResponse escalader(@PathVariable Long reclamationId,
                                         @Valid @RequestBody(required = false) EscaladeRequest request) {
        return reclamationService.escalader(currentUser.getId(), reclamationId,
                request == null ? null : request.motif());
    }

    /** Le client confirme que l'agent IA a résolu son problème. */
    @PostMapping("/resolution")
    public ReclamationResponse declarerResolue(@PathVariable Long reclamationId) {
        return reclamationService.declarerResolue(currentUser.getId(), reclamationId);
    }

    @GetMapping("/historique")
    public List<HistoriqueStatutResponse> historique(@PathVariable Long reclamationId) {
        return reclamationService.historique(currentUser.getId(), reclamationId);
    }
}