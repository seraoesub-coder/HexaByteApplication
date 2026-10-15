package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.*;
import com.example.HexaClientSpace.security.CurrentUser;
import com.example.HexaClientSpace.services.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations/{reclamationId}")
@PreAuthorize("hasRole('CLIENT')")
public class ConversationController {

    private final ConversationService conversationService;
    private final CurrentUser currentUser;

    public ConversationController(ConversationService conversationService, CurrentUser currentUser) {
        this.conversationService = conversationService;
        this.currentUser = currentUser;
    }

    /** Lance l'assistance IA : la description de la réclamation sert de premier message. */
    @PostMapping("/assistance")
    public ResponseEntity<EchangeResponse> demarrer(@PathVariable Long reclamationId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversationService.demarrer(currentUser.getId(), reclamationId));
    }

    @PostMapping("/messages")
    public EchangeResponse envoyer(@PathVariable Long reclamationId,
                                   @Valid @RequestBody EnvoyerMessageRequest request) {
        return conversationService.envoyer(currentUser.getId(), reclamationId, request.contenu());
    }

    @GetMapping("/messages")
    public List<MessageResponse> historique(@PathVariable Long reclamationId) {
        return conversationService.historique(currentUser.getId(), reclamationId);
    }

    @PostMapping("/messages/{messageId}/evaluation")
    public ResponseEntity<EvaluationResponse> evaluer(@PathVariable Long reclamationId,
                                                      @PathVariable Long messageId,
                                                      @Valid @RequestBody EvaluationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversationService.evaluer(currentUser.getId(), reclamationId, messageId, request));
    }
}