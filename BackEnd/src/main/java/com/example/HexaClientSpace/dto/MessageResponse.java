package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.MessageIA;

import java.time.LocalDateTime;

/** note = null tant que la réponse n'a pas été évaluée (et pour les messages du client). */
public record MessageResponse(
        Long id,
        String role,
        String contenu,
        LocalDateTime dateCreation,
        Integer note
) {
    public static MessageResponse from(MessageIA m, Integer note) {
        return new MessageResponse(m.getId(), m.getRole().name(), m.getContenu(), m.getDateCreation(), note);
    }
}