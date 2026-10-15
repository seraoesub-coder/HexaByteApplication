package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.ConversationIA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationIARepository extends JpaRepository<ConversationIA, Long> {

    Optional<ConversationIA> findByReclamationId(Long reclamationId);
}