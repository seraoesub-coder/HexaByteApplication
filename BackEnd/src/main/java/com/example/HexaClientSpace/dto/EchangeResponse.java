package com.example.HexaClientSpace.dto;

/** Résultat d'un tour de dialogue : la question, la réponse de l'agent et le statut courant. */
public record EchangeResponse(
        MessageResponse question,
        MessageResponse reponse,
        String statutReclamation
) {}