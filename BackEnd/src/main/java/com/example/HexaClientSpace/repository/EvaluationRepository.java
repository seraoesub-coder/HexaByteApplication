package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    boolean existsByMessageId(Long messageId);

    List<Evaluation> findByMessageConversationId(Long conversationId);
}