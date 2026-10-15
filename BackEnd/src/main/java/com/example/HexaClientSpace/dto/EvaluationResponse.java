package com.example.HexaClientSpace.dto;

import com.example.HexaClientSpace.entity.Evaluation;
import com.example.HexaClientSpace.entity.StatutReclamation;

import java.time.LocalDateTime;

public record EvaluationResponse(
        Long id,
        Long messageId,
        int note,
        String commentaire,
        LocalDateTime dateEvaluation,
        String statutReclamation
) {
    public static EvaluationResponse from(Evaluation e, StatutReclamation statut) {
        return new EvaluationResponse(e.getId(), e.getMessage().getId(), e.getNote(),
                e.getCommentaire(), e.getDateEvaluation(), statut.name());
    }
}