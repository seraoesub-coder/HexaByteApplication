package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.MessageIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageIARepository extends JpaRepository<MessageIA, Long> {

    List<MessageIA> findByConversationIdOrderByIdAsc(Long conversationId);

    boolean existsByConversationId(Long conversationId);

    /** Ne retrouve le message que s'il appartient bien à la réclamation indiquée. */
    @Query("select m from MessageIA m where m.id = :id and m.conversation.reclamation.id = :reclamationId")
    Optional<MessageIA> findByIdAndReclamationId(@Param("id") Long id,
                                                 @Param("reclamationId") Long reclamationId);
}