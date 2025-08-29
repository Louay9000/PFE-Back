package org.example.pfeback.repository;

import org.example.pfeback.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Récupérer tous les messages entre deux utilisateurs (peu importe qui est sender/receiver)
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) " +
            "   OR (m.sender.id = :user2 AND m.receiver.id = :user1) " +
            "ORDER BY m.id ASC")
    List<ChatMessage> findConversation(@Param("user1") Integer user1,
                                       @Param("user2") Integer user2);


    List<ChatMessage> findByReceiverId(Integer receiverId);

}
