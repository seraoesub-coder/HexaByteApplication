package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.CreateRendezVousRequest;
import com.example.HexaClientSpace.dto.RendezVousResponse;
import com.example.HexaClientSpace.entity.Client;
import com.example.HexaClientSpace.entity.Reclamation;
import com.example.HexaClientSpace.entity.RendezVous;
import com.example.HexaClientSpace.entity.StatutRendezVous;
import com.example.HexaClientSpace.entity.Technicien;
import com.example.HexaClientSpace.exception.RessourceIntrouvableException;
import com.example.HexaClientSpace.repository.ClientRepository;
import com.example.HexaClientSpace.repository.ReclamationRepository;
import com.example.HexaClientSpace.repository.RendezVousRepository;
import com.example.HexaClientSpace.repository.TechnicienRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RendezVousService {

    /** Créneaux d'une heure, espacés d'au moins 2 h : deux créneaux ne se chevauchent jamais. */
    private static final List<LocalTime> CRENEAUX = List.of(
            LocalTime.of(9, 0), LocalTime.of(11, 0), LocalTime.of(14, 0), LocalTime.of(16, 0));
    private static final int DUREE_MINUTES = 60;
    /** Le premier créneau proposé est au plus tôt dans 2 jours. */
    private static final int DELAI_MINIMUM_JOURS = 2;
    /** On cherche un créneau libre sur les 14 jours suivants. */
    private static final int HORIZON_JOURS = 14;
    /** Un rendez-vous confirmé ne s'annule plus moins de 24 h avant l'heure prévue. */
    private static final int DELAI_ANNULATION_HEURES = 24;

    private final RendezVousRepository rendezVousRepository;
    private final TechnicienRepository technicienRepository;
    private final ClientRepository clientRepository;
    private final ReclamationRepository reclamationRepository;

    public RendezVousService(RendezVousRepository rendezVousRepository,
                             TechnicienRepository technicienRepository,
                             ClientRepository clientRepository,
                             ReclamationRepository reclamationRepository) {
        this.rendezVousRepository = rendezVousRepository;
        this.technicienRepository = technicienRepository;
        this.clientRepository = clientRepository;
        this.reclamationRepository = reclamationRepository;
    }

    /** Le client demande un rendez-vous : le système propose un créneau (aucun technicien encore affecté). */
    @Transactional
    public RendezVousResponse demander(Long userId, CreateRendezVousRequest request) {
        Client client = trouverClient(userId);

        String adresse = client.getAdresseInstallation();
        if (adresse == null || adresse.isBlank()) {
            throw new IllegalStateException(
                    "Renseignez votre adresse d'installation dans votre profil avant de demander un rendez-vous.");
        }
        if (rendezVousRepository.existsByClientIdAndStatutAndDateHeureAfter(
                client.getId(), StatutRendezVous.PROPOSE, LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Vous avez déjà une proposition de rendez-vous en attente. Confirmez-la ou annulez-la.");
        }

        Reclamation reclamation = null;
        if (request != null && request.reclamationId() != null) {
            reclamation = reclamationRepository.findByIdAndClientId(request.reclamationId(), client.getId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Réclamation introuvable."));
        }

        RendezVous rdv = new RendezVous();
        rdv.setClient(client);
        rdv.setReclamation(reclamation);
        rdv.setAdresse(adresse.trim());
        rdv.setMotif(request == null || request.motif() == null ? null : request.motif().trim());
        rdv.setDateHeure(proposerCreneau());
        rdv.setDureeMinutes(DUREE_MINUTES);
        rdv.setStatut(StatutRendezVous.PROPOSE);
        // Référence provisoire (colonne NOT NULL) : l'identifiant n'existe qu'après l'INSERT.
        rdv.setReference("TMP-" + UUID.randomUUID().toString().replace("-", ""));

        RendezVous saved = rendezVousRepository.save(rdv);
        saved.setReference(String.format("RDV-%d-%06d", Year.now().getValue(), saved.getId()));
        return RendezVousResponse.from(saved);
    }

    /** Le client confirme la date proposée : un technicien disponible est alors tiré au hasard. */
    @Transactional
    public RendezVousResponse confirmer(Long userId, Long rendezVousId) {
        RendezVous rdv = trouver(userId, rendezVousId);

        if (rdv.getStatut() != StatutRendezVous.PROPOSE) {
            throw new IllegalStateException(
                    "Seule une proposition en attente peut être confirmée (statut : " + rdv.getStatut() + ").");
        }
        if (!rdv.getDateHeure().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Cette proposition a expiré. Demandez un nouveau rendez-vous.");
        }

        List<Technicien> disponibles = technicienRepository.disponiblesLe(rdv.getDateHeure());
        if (disponibles.isEmpty()) {
            throw new IllegalStateException(
                    "Ce créneau n'est plus disponible. Annulez cette proposition et demandez-en une nouvelle.");
        }

        Technicien choisi = disponibles.get(ThreadLocalRandom.current().nextInt(disponibles.size()));
        rdv.setTechnicien(choisi);
        rdv.setStatut(StatutRendezVous.CONFIRME);
        rdv.setDateConfirmation(LocalDateTime.now());
        return RendezVousResponse.from(rdv);
    }

    @Transactional
    public RendezVousResponse annuler(Long userId, Long rendezVousId) {
        RendezVous rdv = trouver(userId, rendezVousId);

        if (!rdv.getStatut().peutPasserA(StatutRendezVous.ANNULE)) {
            throw new IllegalStateException(
                    "Ce rendez-vous ne peut plus être annulé (statut : " + rdv.getStatut() + ").");
        }
        if (rdv.getStatut() == StatutRendezVous.CONFIRME
                && rdv.getDateHeure().isBefore(LocalDateTime.now().plusHours(DELAI_ANNULATION_HEURES))) {
            throw new IllegalStateException(
                    "Un rendez-vous confirmé ne peut plus être annulé moins de "
                    + DELAI_ANNULATION_HEURES + " h avant l'heure prévue.");
        }
        rdv.setStatut(StatutRendezVous.ANNULE);
        return RendezVousResponse.from(rdv);
    }

    @Transactional(readOnly = true)
    public Page<RendezVousResponse> lister(Long userId, Pageable pageable) {
        Client client = trouverClient(userId);
        return rendezVousRepository.findByClientId(client.getId(), pageable).map(RendezVousResponse::from);
    }

    @Transactional(readOnly = true)
    public RendezVousResponse obtenir(Long userId, Long rendezVousId) {
        return RendezVousResponse.from(trouver(userId, rendezVousId));
    }

    // ------------------------------------------------------------------

    /** Premier créneau (jour ouvré, heure fixe) pour lequel au moins un technicien est libre. */
    private LocalDateTime proposerCreneau() {
        LocalDate jour = LocalDate.now().plusDays(DELAI_MINIMUM_JOURS);
        for (int i = 0; i < HORIZON_JOURS; i++, jour = jour.plusDays(1)) {
            if (jour.getDayOfWeek() == DayOfWeek.SATURDAY || jour.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            for (LocalTime heure : CRENEAUX) {
                LocalDateTime candidat = LocalDateTime.of(jour, heure);
                if (!technicienRepository.disponiblesLe(candidat).isEmpty()) {
                    return candidat;
                }
            }
        }
        throw new IllegalStateException("Aucun créneau disponible pour le moment. Veuillez réessayer plus tard.");
    }

    private RendezVous trouver(Long userId, Long rendezVousId) {
        Client client = trouverClient(userId);
        return rendezVousRepository.findByIdAndClientId(rendezVousId, client.getId())
                .orElseThrow(() -> new RessourceIntrouvableException("Rendez-vous introuvable."));
    }

    private Client trouverClient(Long userId) {
        return clientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun profil client rattaché à cet utilisateur."));
    }
}