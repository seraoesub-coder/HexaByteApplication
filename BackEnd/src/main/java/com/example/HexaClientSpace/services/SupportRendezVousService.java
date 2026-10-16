package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.SupportRendezVousResponse;
import com.example.HexaClientSpace.entity.RendezVous;
import com.example.HexaClientSpace.entity.StatutRendezVous;
import com.example.HexaClientSpace.exception.RessourceIntrouvableException;
import com.example.HexaClientSpace.repository.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupportRendezVousService {

    private final RendezVousRepository rendezVousRepository;

    public SupportRendezVousService(RendezVousRepository rendezVousRepository) {
        this.rendezVousRepository = rendezVousRepository;
    }

    @Transactional(readOnly = true)
    public Page<SupportRendezVousResponse> lister(StatutRendezVous statut, Pageable pageable) {
        return rendezVousRepository.findByStatut(statut, pageable).map(SupportRendezVousResponse::from);
    }

    /**
     * CONFIRME -> REALISE. La date prévue n'est volontairement pas contrôlée, pour pouvoir tester
     * sans attendre le jour du rendez-vous ; en production, refuser si dateHeure est dans le futur.
     */
    @Transactional
    public SupportRendezVousResponse marquerRealise(Long rendezVousId) {
        RendezVous rdv = rendezVousRepository.findDetailById(rendezVousId)
                .orElseThrow(() -> new RessourceIntrouvableException("Rendez-vous introuvable."));

        if (!rdv.getStatut().peutPasserA(StatutRendezVous.REALISE)) {
            throw new IllegalStateException(
                    "Seul un rendez-vous confirmé peut être marqué comme réalisé (statut : " + rdv.getStatut() + ").");
        }
        rdv.setStatut(StatutRendezVous.REALISE);
        return SupportRendezVousResponse.from(rdv);
    }
}