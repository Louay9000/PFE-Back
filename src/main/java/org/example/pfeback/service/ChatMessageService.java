package org.example.pfeback.service;


import lombok.RequiredArgsConstructor;
import org.example.pfeback.model.ChatMessage;
import org.example.pfeback.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    // Récupérer tous les messages reçus par un utilisateur
    public List<ChatMessage> getMessagesForUser(Integer receiverId) {
        return chatMessageRepository.findByReceiverId(receiverId);
    }
}
