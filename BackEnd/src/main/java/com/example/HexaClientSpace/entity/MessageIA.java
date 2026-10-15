package com.example.HexaClientSpace.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Un tour de parole : message du client (USER) ou réponse de l'agent (ASSISTANT). */
@Entity
@Table(name = "messages_ia",
       indexes = @Index(name = "idx_messages_conversation", columnList = "conversation_id"))
public class MessageIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_messages_conversation"))
    private ConversationIA conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private RoleMessage role;

    @Column(nullable = false, length = 4000)
    private String contenu;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public ConversationIA getConversation() { return conversation; }
    public void setConversation(ConversationIA conversation) { this.conversation = conversation; }

    public RoleMessage getRole() { return role; }
    public void setRole(RoleMessage role) { this.role = role; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}