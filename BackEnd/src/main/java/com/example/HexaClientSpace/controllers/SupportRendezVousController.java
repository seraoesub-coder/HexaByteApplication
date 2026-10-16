package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.dto.SupportRendezVousResponse;
import com.example.HexaClientSpace.entity.StatutRendezVous;
import com.example.HexaClientSpace.services.SupportRendezVousService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support/rendez-vous")
@PreAuthorize("hasAnyRole('AGENT_SUPPORT','ADMIN')")
public class SupportRendezVousController {

    private final SupportRendezVousService supportRendezVousService;

    public SupportRendezVousController(SupportRendezVousService supportRendezVousService) {
        this.supportRendezVousService = supportRendezVousService;
    }

    /** Par défaut : les rendez-vous confirmés, les plus proches d'abord. */
    @GetMapping
    public Page<SupportRendezVousResponse> lister(
            @RequestParam(defaultValue = "CONFIRME") StatutRendezVous statut,
            @PageableDefault(size = 20, sort = "dateHeure", direction = Sort.Direction.ASC) Pageable pageable) {
        return supportRendezVousService.lister(statut, pageable);
    }

    @PostMapping("/{id}/realisation")
    public SupportRendezVousResponse marquerRealise(@PathVariable Long id) {
        return supportRendezVousService.marquerRealise(id);
    }
}