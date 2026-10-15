package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.CreateReclamationRequest;
import com.example.HexaClientSpace.dto.ReclamationResponse;
import com.example.HexaClientSpace.security.CurrentUser;
import com.example.HexaClientSpace.services.ReclamationService;
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
@RequestMapping("/api/reclamations")
@PreAuthorize("hasRole('CLIENT')")
public class ReclamationController {

    private final ReclamationService reclamationService;
    private final CurrentUser currentUser;

    public ReclamationController(ReclamationService reclamationService, CurrentUser currentUser) {
        this.reclamationService = reclamationService;
        this.currentUser = currentUser;
    }

    /** Dépôt d'une réclamation par le client connecté. */
    @PostMapping
    public ResponseEntity<ReclamationResponse> creer(@Valid @RequestBody CreateReclamationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reclamationService.creer(currentUser.getId(), request));
    }

    /** Mes réclamations, les plus récentes d'abord. Paramètres : ?page=0&size=10 */
    @GetMapping
    public Page<ReclamationResponse> mesReclamations(
            @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {
        return reclamationService.lister(currentUser.getId(), pageable);
    }

    @GetMapping("/{id}")
    public ReclamationResponse detail(@PathVariable Long id) {
        return reclamationService.obtenir(currentUser.getId(), id);
    }
}