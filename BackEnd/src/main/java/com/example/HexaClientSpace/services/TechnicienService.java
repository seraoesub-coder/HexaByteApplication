package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.TechnicienAdminResponse;
import com.example.HexaClientSpace.dto.TechnicienRequest;
import com.example.HexaClientSpace.entity.Technicien;
import com.example.HexaClientSpace.exception.RessourceIntrouvableException;
import com.example.HexaClientSpace.repository.RendezVousRepository;
import com.example.HexaClientSpace.repository.TechnicienRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TechnicienService {

    private final TechnicienRepository technicienRepository;
    private final RendezVousRepository rendezVousRepository;

    public TechnicienService(TechnicienRepository technicienRepository,
                             RendezVousRepository rendezVousRepository) {
        this.technicienRepository = technicienRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    @Transactional(readOnly = true)
    public Page<TechnicienAdminResponse> lister(Boolean actif, Pageable pageable) {
        Page<Technicien> page = (actif == null)
                ? technicienRepository.findAll(pageable)
                : technicienRepository.findByActif(actif, pageable);
        return page.map(TechnicienAdminResponse::from);
    }

    @Transactional(readOnly = true)
    public TechnicienAdminResponse obtenir(Long id) {
        return TechnicienAdminResponse.from(trouver(id));
    }

    @Transactional
    public TechnicienAdminResponse creer(TechnicienRequest request) {
        String telephone = normaliser(request.telephone());
        if (technicienRepository.existsByTelephone(telephone)) {
            throw new IllegalStateException("Un technicien existe déjà avec ce numéro de téléphone.");
        }

        Technicien technicien = new Technicien();
        appliquer(technicien, request, telephone);
        technicien.setActif(request.actif() == null || request.actif());
        return TechnicienAdminResponse.from(technicienRepository.save(technicien));
    }

    /** Modification complète (PUT) : tous les champs sont remplacés ; actif absent = inchangé. */
    @Transactional
    public TechnicienAdminResponse modifier(Long id, TechnicienRequest request) {
        Technicien technicien = trouver(id);

        String telephone = normaliser(request.telephone());
        if (technicienRepository.existsByTelephoneAndIdNot(telephone, id)) {
            throw new IllegalStateException("Un autre technicien utilise déjà ce numéro de téléphone.");
        }

        appliquer(technicien, request, telephone);
        if (request.actif() != null) {
            technicien.setActif(request.actif());
        }
        return TechnicienAdminResponse.from(technicien);
    }

    /**
     * Suppression définitive, possible uniquement si le technicien n'a jamais eu de rendez-vous :
     * sinon l'historique des interventions serait perdu. Dans ce cas on le désactive (actif = false).
     */
    @Transactional
    public void supprimer(Long id) {
        Technicien technicien = trouver(id);
        if (rendezVousRepository.existsByTechnicienId(id)) {
            throw new IllegalStateException(
                    "Ce technicien a des rendez-vous enregistrés : désactivez-le (actif = false) plutôt que de le supprimer.");
        }
        technicienRepository.delete(technicien);
    }

    // ------------------------------------------------------------------

    private void appliquer(Technicien technicien, TechnicienRequest request, String telephone) {
        technicien.setNom(request.nom().trim());
        technicien.setPrenom(request.prenom().trim());
        technicien.setTelephone(telephone);
        technicien.setNiveauExperience(request.niveauExperience());
    }

    /** "+216 22 123 456" et "22123456" désignent le même numéro : on stocke toujours les 8 chiffres. */
    private String normaliser(String telephone) {
        String t = telephone.replaceAll("\\s", "");
        return t.startsWith("+216") ? t.substring(4) : t;
    }

    private Technicien trouver(Long id) {
        return technicienRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Technicien introuvable."));
    }
}