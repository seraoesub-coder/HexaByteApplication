package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.EntreeCarnetResponse;
import com.example.HexaClientSpace.dto.MessageResponse;
import com.example.HexaClientSpace.dto.SupportReclamationResponse;
import com.example.HexaClientSpace.entity.*;
import com.example.HexaClientSpace.exception.RessourceIntrouvableException;
import com.example.HexaClientSpace.repository.ConversationIARepository;
import com.example.HexaClientSpace.repository.EntreeCarnetRepository;
import com.example.HexaClientSpace.repository.EvaluationRepository;
import com.example.HexaClientSpace.repository.MessageIARepository;
import com.example.HexaClientSpace.repository.ReclamationRepository;
import com.example.HexaClientSpace.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Opérations de l'agent support. Aucune vérification de propriétaire : c'est voulu, l'accès est limité par le rôle. */
@Service
public class SupportReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final ConversationIARepository conversationRepository;
    private final MessageIARepository messageRepository;
    private final EvaluationRepository evaluationRepository;
    private final EntreeCarnetRepository carnetRepository;
    private final UserRepository userRepository;
    private final ReclamationStatutService statutService;

    public SupportReclamationService(ReclamationRepository reclamationRepository,
                                     ConversationIARepository conversationRepository,
                                     MessageIARepository messageRepository,
                                     EvaluationRepository evaluationRepository,
                                     EntreeCarnetRepository carnetRepository,
                                     UserRepository userRepository,
                                     ReclamationStatutService statutService) {
        this.reclamationRepository = reclamationRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.evaluationRepository = evaluationRepository;
        this.carnetRepository = carnetRepository;
        this.userRepository = userRepository;
        this.statutService = statutService;
    }

    @Transactional(readOnly = true)
    public Page<SupportReclamationResponse> file(StatutReclamation statut, Pageable pageable) {
        return reclamationRepository.findByStatut(statut, pageable).map(SupportReclamationResponse::from);
    }

    @Transactional(readOnly = true)
    public SupportReclamationResponse detail(Long reclamationId) {
        return SupportReclamationResponse.from(trouver(reclamationId));
    }

    /** Conversation complète (client, agent IA, support) : l'agent n'a pas à faire répéter le client. */
    @Transactional(readOnly = true)
    public List<MessageResponse> conversation(Long reclamationId) {
        Reclamation reclamation = trouver(reclamationId);
        return conversationRepository.findByReclamationId(reclamation.getId())
                .map(conversation -> {
                    Map<Long, Integer> notes = evaluationRepository
                            .findByMessageConversationId(conversation.getId()).stream()
                            .collect(Collectors.toMap(e -> e.getMessage().getId(), Evaluation::getNote));
                    return messageRepository.findByConversationIdOrderByIdAsc(conversation.getId()).stream()
                            .map(m -> MessageResponse.from(m, notes.get(m.getId())))
                            .toList();
                })
                .orElse(List.of());
    }

    /** Réponse de l'agent : ajoutée à la conversation, visible par le client dans GET /messages. */
    @Transactional
    public MessageResponse repondre(Long reclamationId, String contenu) {
        Reclamation reclamation = trouver(reclamationId);
        if (reclamation.getStatut() != StatutReclamation.ESCALADEE_SUPPORT) {
            throw new IllegalStateException(
                    "Le support ne répond qu'aux réclamations escaladées (statut : " + reclamation.getStatut() + ").");
        }

        // La conversation peut ne pas exister : escalade manuelle sans passage par l'IA.
        ConversationIA conversation = conversationRepository.findByReclamationId(reclamation.getId())
                .orElseGet(() -> {
                    ConversationIA c = new ConversationIA();
                    c.setReclamation(reclamation);
                    return conversationRepository.save(c);
                });

        MessageIA message = new MessageIA();
        message.setConversation(conversation);
        message.setRole(RoleMessage.SUPPORT);
        message.setContenu(contenu.trim());
        return MessageResponse.from(messageRepository.save(message), null);
    }

    /** Clôture une réclamation escaladée (ou résolue par l'IA). L'agent est tracé dans le motif de l'historique. */
    @Transactional
    public SupportReclamationResponse cloturer(Long agentUserId, Long reclamationId, String motif) {
        Reclamation reclamation = trouver(reclamationId);
        statutService.changer(reclamation, StatutReclamation.CLOTUREE, ActeurStatut.SUPPORT,
                "Agent #" + agentUserId + " : " + motif.trim());
        return SupportReclamationResponse.from(reclamation);
    }

    // ----------------------- Carnet de suivi interne -----------------------

    @Transactional
    public EntreeCarnetResponse ajouterEntree(Long agentUserId, Long reclamationId, String contenu) {
        Reclamation reclamation = trouver(reclamationId);
        return EntreeCarnetResponse.from(creerEntree(reclamation, agentUserId, contenu.trim(), false));
    }

    @Transactional(readOnly = true)
    public List<EntreeCarnetResponse> carnet(Long reclamationId) {
        Reclamation reclamation = trouver(reclamationId);
        return carnetRepository.findByReclamationId(reclamation.getId()).stream()
                .map(EntreeCarnetResponse::from)
                .toList();
    }

    // ----------------------- Priorité -----------------------

    /**
     * Change la priorité (idempotent : même priorité = aucun changement).
     * Chaque changement laisse une entrée automatique dans le carnet : qui, quand, pourquoi.
     */
    @Transactional
    public SupportReclamationResponse changerPriorite(Long agentUserId, Long reclamationId,
                                                      PrioriteReclamation nouvelle, String motif) {
        Reclamation reclamation = trouver(reclamationId);

        if (reclamation.getStatut() == StatutReclamation.CLOTUREE) {
            throw new IllegalStateException("Impossible de modifier la priorité d'une réclamation clôturée.");
        }

        PrioriteReclamation ancienne = reclamation.getPriorite();
        if (ancienne != nouvelle) {
            reclamation.setPriorite(nouvelle);
            String trace = "Priorité modifiée : de " + ancienne + " à " + nouvelle + "."
                    + (motif == null || motif.isBlank() ? "" : " Motif : " + motif.trim());
            creerEntree(reclamation, agentUserId, trace, true);
        }
        return SupportReclamationResponse.from(reclamation);
    }

    // ------------------------------------------------------------------

    private EntreeCarnet creerEntree(Reclamation reclamation, Long agentUserId, String contenu, boolean automatique) {
        User auteur = userRepository.findById(agentUserId)
                .orElseThrow(() -> new IllegalStateException("Compte agent introuvable."));
        EntreeCarnet entree = new EntreeCarnet();
        entree.setReclamation(reclamation);
        entree.setAuteur(auteur);
        entree.setContenu(contenu);
        entree.setAutomatique(automatique);
        return carnetRepository.save(entree);
    }

    private Reclamation trouver(Long reclamationId) {
        return reclamationRepository.findDetailById(reclamationId)
                .orElseThrow(() -> new RessourceIntrouvableException("Réclamation introuvable."));
    }
}