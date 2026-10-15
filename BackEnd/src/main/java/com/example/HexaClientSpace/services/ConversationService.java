package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.*;
import com.example.HexaClientSpace.entity.*;
import com.example.HexaClientSpace.exception.RessourceIntrouvableException;
import com.example.HexaClientSpace.repository.*;
import com.example.HexaClientSpace.services.ia.ChatbotService;
import com.example.HexaClientSpace.services.ia.PromptBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    /** Garde-fou de coût : au-delà, la suite relève d'un conseiller humain. */
    private static final int MAX_MESSAGES_PAR_CONVERSATION = 40;
    /** Nombre de messages passés au modèle comme contexte. */
    private static final int MESSAGES_DE_CONTEXTE = 10;
    private static final int LONGUEUR_MAX_MESSAGE = 4000;
    /** Une note inférieure ou égale à ce seuil déclenche l'escalade vers le support humain. */
    private static final int SEUIL_ESCALADE = 2;

    private static final Set<StatutReclamation> STATUTS_FERMES = Set.of(
            StatutReclamation.RESOLUE_PAR_IA,
            StatutReclamation.ESCALADEE_SUPPORT,
            StatutReclamation.CLOTUREE);

    private final ClientRepository clientRepository;
    private final ReclamationRepository reclamationRepository;
    private final ConversationIARepository conversationRepository;
    private final MessageIARepository messageRepository;
    private final EvaluationRepository evaluationRepository;
    private final ChatbotService chatbotService;
    private final PromptBuilder promptBuilder;
    private final ReclamationStatutService statutService;

    public ConversationService(ClientRepository clientRepository,
                               ReclamationRepository reclamationRepository,
                               ConversationIARepository conversationRepository,
                               MessageIARepository messageRepository,
                               EvaluationRepository evaluationRepository,
                               ChatbotService chatbotService,
                               PromptBuilder promptBuilder,
                               ReclamationStatutService statutService) {
        this.clientRepository = clientRepository;
        this.reclamationRepository = reclamationRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.evaluationRepository = evaluationRepository;
        this.chatbotService = chatbotService;
        this.promptBuilder = promptBuilder;
        this.statutService = statutService;
    }

    /**
     * Démarre l'assistance : la description de la réclamation devient le premier message.
     * Note : la transaction reste ouverte pendant l'appel au modèle (quelques secondes).
     * Acceptable à cette échelle ; à découpler si la charge augmente.
     */
    @Transactional
    public EchangeResponse demarrer(Long userId, Long reclamationId) {
        Reclamation reclamation = trouverReclamation(userId, reclamationId);
        verifierOuverte(reclamation);

        ConversationIA conversation = conversationRepository.findByReclamationId(reclamation.getId())
                .orElseGet(() -> {
                    ConversationIA c = new ConversationIA();
                    c.setReclamation(reclamation);
                    return conversationRepository.save(c);
                });

        if (messageRepository.existsByConversationId(conversation.getId())) {
            throw new IllegalStateException("L'assistance a déjà démarré pour cette réclamation.");
        }
        return echanger(reclamation, conversation, reclamation.getDescription());
    }

    @Transactional
    public EchangeResponse envoyer(Long userId, Long reclamationId, String contenu) {
        Reclamation reclamation = trouverReclamation(userId, reclamationId);
        verifierOuverte(reclamation);

        ConversationIA conversation = conversationRepository.findByReclamationId(reclamation.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Démarrez d'abord l'assistance pour cette réclamation."));

        return echanger(reclamation, conversation, contenu.trim());
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> historique(Long userId, Long reclamationId) {
        Reclamation reclamation = trouverReclamation(userId, reclamationId);

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

    @Transactional
    public EvaluationResponse evaluer(Long userId, Long reclamationId, Long messageId,
                                      EvaluationRequest request) {
        Reclamation reclamation = trouverReclamation(userId, reclamationId);

        MessageIA message = messageRepository.findByIdAndReclamationId(messageId, reclamation.getId())
                .orElseThrow(() -> new RessourceIntrouvableException("Message introuvable."));

        if (message.getRole() != RoleMessage.ASSISTANT) {
            throw new IllegalStateException("Seules les réponses de l'assistant peuvent être évaluées.");
        }
        if (evaluationRepository.existsByMessageId(message.getId())) {
            throw new IllegalStateException("Cette réponse a déjà été évaluée.");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setMessage(message);
        evaluation.setNote(request.note());
        evaluation.setCommentaire(request.commentaire() == null ? null : request.commentaire().trim());

        Evaluation enregistree = evaluationRepository.save(evaluation);

        // Mauvaise note : la réclamation passe automatiquement au support humain.
        if (request.note() <= SEUIL_ESCALADE
                && reclamation.getStatut().peutPasserA(StatutReclamation.ESCALADEE_SUPPORT)) {
            statutService.changer(reclamation, StatutReclamation.ESCALADEE_SUPPORT, ActeurStatut.SYSTEME,
                    "Note de satisfaction insuffisante (" + request.note() + "/5).");
        }

        return EvaluationResponse.from(enregistree, reclamation.getStatut());
    }

    // ------------------------------------------------------------------

    private EchangeResponse echanger(Reclamation reclamation, ConversationIA conversation, String contenu) {
        List<MessageIA> historique = messageRepository.findByConversationIdOrderByIdAsc(conversation.getId());
        if (historique.size() >= MAX_MESSAGES_PAR_CONVERSATION) {
            throw new IllegalStateException(
                    "Limite d'échanges atteinte pour cette réclamation. Un conseiller humain pourra prendre le relais.");
        }

        List<MessageIA> contexte = historique.subList(
                Math.max(0, historique.size() - MESSAGES_DE_CONTEXTE), historique.size());

        // Appel au modèle AVANT toute écriture : s'il échoue, rien n'est enregistré.
        String reponse = chatbotService.repondre(
                promptBuilder.systemPrompt(reclamation),
                promptBuilder.userPrompt(contexte, contenu));
        if (reponse.length() > LONGUEUR_MAX_MESSAGE) {
            reponse = reponse.substring(0, LONGUEUR_MAX_MESSAGE);
        }

        MessageIA question = enregistrer(conversation, RoleMessage.USER, contenu);
        MessageIA reponseAssistant = enregistrer(conversation, RoleMessage.ASSISTANT, reponse);

        if (reclamation.getStatut() == StatutReclamation.OUVERTE) {
            statutService.changer(reclamation, StatutReclamation.EN_COURS_IA,
                    ActeurStatut.SYSTEME, "Première réponse de l'agent IA");
        }

        return new EchangeResponse(
                MessageResponse.from(question, null),
                MessageResponse.from(reponseAssistant, null),
                reclamation.getStatut().name());
    }

    private MessageIA enregistrer(ConversationIA conversation, RoleMessage role, String contenu) {
        MessageIA m = new MessageIA();
        m.setConversation(conversation);
        m.setRole(role);
        m.setContenu(contenu);
        return messageRepository.save(m);
    }

    private Reclamation trouverReclamation(Long userId, Long reclamationId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun profil client rattaché à cet utilisateur."));
        return reclamationRepository.findByIdAndClientId(reclamationId, client.getId())
                .orElseThrow(() -> new RessourceIntrouvableException("Réclamation introuvable."));
    }

    private void verifierOuverte(Reclamation reclamation) {
        if (STATUTS_FERMES.contains(reclamation.getStatut())) {
            throw new IllegalStateException(
                    "Cette réclamation n'accepte plus de messages (statut : " + reclamation.getStatut() + ").");
        }
    }
}