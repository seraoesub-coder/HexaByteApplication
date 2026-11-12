package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.CreateReclamationRequest;
import com.example.HexaClientSpace.dto.HistoriqueStatutResponse;
import com.example.HexaClientSpace.dto.ReclamationResponse;
import com.example.HexaClientSpace.entity.ActeurStatut;
import com.example.HexaClientSpace.entity.PrioriteReclamation;
import com.example.HexaClientSpace.entity.Client;
import com.example.HexaClientSpace.entity.Reclamation;
import com.example.HexaClientSpace.entity.SousCategorie;
import com.example.HexaClientSpace.entity.StatutReclamation;
import com.example.HexaClientSpace.exception.RessourceIntrouvableException;
import com.example.HexaClientSpace.repository.ClientRepository;
import com.example.HexaClientSpace.repository.HistoriqueStatutRepository;
import com.example.HexaClientSpace.repository.ReclamationRepository;
import com.example.HexaClientSpace.repository.SousCategorieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ReclamationService {

    /** Plafond de réclamations urgentes en cours par client : évite qu'« urgent » devienne la norme. */
    private static final int MAX_URGENTES_EN_COURS = 2;
    private static final Set<StatutReclamation> STATUTS_EN_COURS = Set.of(
            StatutReclamation.OUVERTE, StatutReclamation.EN_COURS_IA, StatutReclamation.ESCALADEE_SUPPORT);

    private final ReclamationRepository reclamationRepository;
    private final SousCategorieRepository sousCategorieRepository;
    private final ClientRepository clientRepository;
    private final HistoriqueStatutRepository historiqueRepository;
    private final ReclamationStatutService statutService;

    public ReclamationService(ReclamationRepository reclamationRepository,
                              SousCategorieRepository sousCategorieRepository,
                              ClientRepository clientRepository,
                              HistoriqueStatutRepository historiqueRepository,
                              ReclamationStatutService statutService) {
        this.reclamationRepository = reclamationRepository;
        this.sousCategorieRepository = sousCategorieRepository;
        this.clientRepository = clientRepository;
        this.historiqueRepository = historiqueRepository;
        this.statutService = statutService;
    }

    @Transactional
    public ReclamationResponse creer(Long userId, CreateReclamationRequest request) {
        Client client = trouverClient(userId);

        boolean urgente = Boolean.TRUE.equals(request.urgent());
        if (urgente && reclamationRepository.countByClientIdAndPrioriteAndStatutIn(
                client.getId(), PrioriteReclamation.HAUTE, STATUTS_EN_COURS) >= MAX_URGENTES_EN_COURS) {
            throw new IllegalStateException("Vous avez déjà " + MAX_URGENTES_EN_COURS
                    + " réclamations urgentes en cours. Attendez leur traitement, ou déposez celle-ci sans l'indiquer comme urgente.");
        }

        SousCategorie sousCategorie = sousCategorieRepository.findById(request.sousCategorieId())
                .filter(SousCategorie::isActif)
                .filter(sc -> sc.getCategorie().isActif())
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Sous-catégorie introuvable ou inactive."));

        Reclamation reclamation = new Reclamation();
        reclamation.setClient(client);
        reclamation.setSousCategorie(sousCategorie);
        reclamation.setDescription(request.description().trim());
        reclamation.setStatut(StatutReclamation.OUVERTE);
        // Référence provisoire (colonne NOT NULL) : l'identifiant n'existe qu'après l'INSERT.
        reclamation.setReference("TMP-" + UUID.randomUUID().toString().replace("-", ""));

        Reclamation saved = reclamationRepository.save(reclamation);
        saved.setReference(String.format("REC-%d-%06d", Year.now().getValue(), saved.getId()));
        statutService.enregistrerCreation(saved);

        if (urgente) {
            // Priorité HAUTE et transmission directe à un humain : l'agent IA est court-circuité.
            saved.setPriorite(PrioriteReclamation.HAUTE);
            statutService.changer(saved, StatutReclamation.ESCALADEE_SUPPORT, ActeurStatut.CLIENT,
                    "Demande urgente : transmise directement à un conseiller humain.");
        }

        return ReclamationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReclamationResponse> lister(Long userId, Pageable pageable) {
        Client client = trouverClient(userId);
        return reclamationRepository.findByClientId(client.getId(), pageable)
                .map(ReclamationResponse::from);
    }

    @Transactional(readOnly = true)
    public ReclamationResponse obtenir(Long userId, Long reclamationId) {
        return ReclamationResponse.from(trouverReclamation(userId, reclamationId));
    }

    /** Le client demande explicitement un conseiller humain. */
    @Transactional
    public ReclamationResponse escalader(Long userId, Long reclamationId, String motif) {
        Reclamation reclamation = trouverReclamation(userId, reclamationId);
        String motifFinal = (motif == null || motif.isBlank())
                ? "Le client a demandé à parler à un conseiller humain."
                : motif.trim();
        statutService.changer(reclamation, StatutReclamation.ESCALADEE_SUPPORT, ActeurStatut.CLIENT, motifFinal);
        return ReclamationResponse.from(reclamation);
    }

    /** Le client confirme que l'agent IA a résolu son problème (indicateur clé du mémoire). */
    @Transactional
    public ReclamationResponse declarerResolue(Long userId, Long reclamationId) {
        Reclamation reclamation = trouverReclamation(userId, reclamationId);
        statutService.changer(reclamation, StatutReclamation.RESOLUE_PAR_IA, ActeurStatut.CLIENT,
                "Le client confirme que le problème est résolu.");
        return ReclamationResponse.from(reclamation);
    }

    @Transactional(readOnly = true)
    public List<HistoriqueStatutResponse> historique(Long userId, Long reclamationId) {
        Reclamation reclamation = trouverReclamation(userId, reclamationId);
        return historiqueRepository.findByReclamationIdOrderByIdAsc(reclamation.getId()).stream()
                .map(HistoriqueStatutResponse::from)
                .toList();
    }

    private Reclamation trouverReclamation(Long userId, Long reclamationId) {
        Client client = trouverClient(userId);
        return reclamationRepository.findByIdAndClientId(reclamationId, client.getId())
                .orElseThrow(() -> new RessourceIntrouvableException("Réclamation introuvable."));
    }

    private Client trouverClient(Long userId) {
        return clientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun profil client rattaché à cet utilisateur."));
    }
}