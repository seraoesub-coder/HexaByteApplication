package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.entity.ActeurStatut;
import com.example.HexaClientSpace.entity.HistoriqueStatut;
import com.example.HexaClientSpace.entity.Reclamation;
import com.example.HexaClientSpace.entity.StatutReclamation;
import com.example.HexaClientSpace.repository.HistoriqueStatutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Point UNIQUE de changement de statut : vérifie la transition autorisée
 * et journalise chaque changement. Doit être appelé dans une transaction existante.
 */
@Service
public class ReclamationStatutService {

    private final HistoriqueStatutRepository historiqueRepository;

    public ReclamationStatutService(HistoriqueStatutRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void enregistrerCreation(Reclamation reclamation) {
        journaliser(reclamation, null, reclamation.getStatut(), ActeurStatut.CLIENT, "Réclamation déposée");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changer(Reclamation reclamation, StatutReclamation nouveau, ActeurStatut acteur, String motif) {
        StatutReclamation ancien = reclamation.getStatut();
        if (!ancien.peutPasserA(nouveau)) {
            throw new IllegalStateException("Action impossible : la réclamation est au statut "
                    + ancien + " et ne peut pas passer à " + nouveau + ".");
        }
        reclamation.setStatut(nouveau);
        journaliser(reclamation, ancien, nouveau, acteur, motif);
    }

    private void journaliser(Reclamation reclamation, StatutReclamation ancien, StatutReclamation nouveau,
                             ActeurStatut acteur, String motif) {
        HistoriqueStatut h = new HistoriqueStatut();
        h.setReclamation(reclamation);
        h.setAncienStatut(ancien);
        h.setNouveauStatut(nouveau);
        h.setActeur(acteur);
        h.setMotif(motif == null ? null : (motif.length() > 255 ? motif.substring(0, 255) : motif));
        historiqueRepository.save(h);
    }
}