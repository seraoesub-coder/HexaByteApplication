package com.example.HexaClientSpace.services.ia;

import com.example.HexaClientSpace.exception.ChatbotIndisponibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OpenAiChatbotService implements ChatbotService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiChatbotService.class);

    private final ChatClient chatClient;

    public OpenAiChatbotService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String repondre(String systemPrompt, String userPrompt) {
        try {
            String reponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            if (reponse == null || reponse.isBlank()) {
                throw new ChatbotIndisponibleException("Réponse vide du modèle");
            }
            return reponse.trim();

        } catch (ChatbotIndisponibleException e) {
            throw e;
        } catch (RuntimeException e) {
            // Le détail (clé, quota, réseau...) reste côté serveur, jamais renvoyé au client.
            log.error("Échec de l'appel au modèle d'IA : {}", e.getMessage());
            throw new ChatbotIndisponibleException("Appel au modèle impossible");
        }
    }
}