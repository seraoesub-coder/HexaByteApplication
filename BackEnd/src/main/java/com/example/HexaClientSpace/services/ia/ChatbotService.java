package com.example.HexaClientSpace.services.ia;

/**
 * Abstraction du fournisseur d'IA : changer d'API (OpenAI, Groq, Gemini...)
 * ne demande que de fournir une autre implémentation de cette interface.
 */
public interface ChatbotService {

    /**
     * @param systemPrompt consignes et contexte de la réclamation
     * @param userPrompt   historique récent + nouveau message du client
     * @return le texte de la réponse
     * @throws com.example.HexaClientSpace.exception.ChatbotIndisponibleException si l'appel échoue
     */
    String repondre(String systemPrompt, String userPrompt);
}