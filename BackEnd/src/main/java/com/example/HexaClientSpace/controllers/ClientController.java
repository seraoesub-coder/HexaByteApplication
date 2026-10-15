package com.example.HexaClientSpace.controllers;


import com.example.HexaClientSpace.dto.AuthResponse;
import com.example.HexaClientSpace.dto.LoginRequest;
import com.example.HexaClientSpace.dto.RegisterRequest;

import com.example.HexaClientSpace.dto.ClientProfileResponse;
import com.example.HexaClientSpace.services.ClientService;
import com.example.HexaClientSpace.security.*;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;
    private final CurrentUser currentUser;

    public ClientController(ClientService clientService, CurrentUser currentUser) {
        this.clientService = clientService;
        this.currentUser = currentUser;
    }

    /** Profil de l'abonne connecte. */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientProfileResponse> monProfil() {
        return ResponseEntity.ok(clientService.getProfil(currentUser.getId()));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientProfileResponse> modifierProfil(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(clientService.mettreAJourCoordonnees(
                currentUser.getId(),
                body.get("telephone"),
                body.get("adresseInstallation"),
                body.get("gouvernorat")));
    }
}
