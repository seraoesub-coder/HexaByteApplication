package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.ChangerPrioriteRequest;
import com.example.HexaClientSpace.dto.ClotureRequest;
import com.example.HexaClientSpace.dto.EntreeCarnetRequest;
import com.example.HexaClientSpace.dto.EntreeCarnetResponse;
import com.example.HexaClientSpace.dto.MessageResponse;
import com.example.HexaClientSpace.dto.ReponseSupportRequest;
import com.example.HexaClientSpace.dto.SupportReclamationResponse;
import com.example.HexaClientSpace.entity.StatutReclamation;
import com.example.HexaClientSpace.security.CurrentUser;
import com.example.HexaClientSpace.services.SupportReclamationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support/reclamations")
@PreAuthorize("hasAnyRole('AGENT_SUPPORT','ADMIN')")
public class SupportReclamationController {

    private final SupportReclamationService supportService;
    private final CurrentUser currentUser;

    public SupportReclamationController(SupportReclamationService supportService, CurrentUser currentUser) {
        this.supportService = supportService;
        this.currentUser = currentUser;
    }

    /**
     * File de travail : par défaut les réclamations escaladées, triées par priorité
     * (HAUTE d'abord) puis par ancienneté. Le tri est défini dans la requête.
     */
    @GetMapping
    public Page<SupportReclamationResponse> file(
            @RequestParam(defaultValue = "ESCALADEE_SUPPORT") StatutReclamation statut,
            @PageableDefault(size = 20) Pageable pageable) {
        return supportService.file(statut, pageable);
    }

    @GetMapping("/{id}")
    public SupportReclamationResponse detail(@PathVariable Long id) {
        return supportService.detail(id);
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponse> conversation(@PathVariable Long id) {
        return supportService.conversation(id);
    }

    @PostMapping("/{id}/reponse")
    public ResponseEntity<MessageResponse> repondre(@PathVariable Long id,
                                                    @Valid @RequestBody ReponseSupportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supportService.repondre(id, request.contenu()));
    }

    @PostMapping("/{id}/cloture")
    public SupportReclamationResponse cloturer(@PathVariable Long id,
                                               @Valid @RequestBody ClotureRequest request) {
        return supportService.cloturer(currentUser.getId(), id, request.motif());
    }

    /** Carnet de suivi interne : réservé au personnel, jamais visible par le client. */
    @GetMapping("/{id}/carnet")
    public List<EntreeCarnetResponse> carnet(@PathVariable Long id) {
        return supportService.carnet(id);
    }

    @PostMapping("/{id}/carnet")
    public ResponseEntity<EntreeCarnetResponse> ajouterEntree(@PathVariable Long id,
                                                              @Valid @RequestBody EntreeCarnetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supportService.ajouterEntree(currentUser.getId(), id, request.contenu()));
    }

    @PatchMapping("/{id}/priorite")
    public SupportReclamationResponse changerPriorite(@PathVariable Long id,
                                                      @Valid @RequestBody ChangerPrioriteRequest request) {
        return supportService.changerPriorite(currentUser.getId(), id, request.priorite(), request.motif());
    }
}