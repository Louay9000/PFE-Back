package org.example.pfeback.controller;

import lombok.RequiredArgsConstructor;
import org.example.pfeback.DTO.ChatMessageDTO;
import org.example.pfeback.DTO.UserDTO;
import org.example.pfeback.model.ChatMessage;
import org.example.pfeback.model.User;
import org.example.pfeback.repository.ChatMessageRepository;
import org.example.pfeback.repository.UserRepository;
import org.example.pfeback.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/talk")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMessageService chatMessageService;


    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public void processMessage(ChatMessage chatMessage) {
        // Envoyer le message en temps réel
        messagingTemplate.convertAndSend("/topic/messages", chatMessage);
    }



    @PostMapping("/add")
    @ResponseBody
    public ChatMessageDTO addMessage(@RequestParam Integer senderId,
                                     @RequestParam Integer receiverId,
                                     @RequestParam String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setContent(content);

        ChatMessage saved = chatMessageRepository.save(chatMessage);

        // Création du DTO
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(saved.getId());
        dto.setContent(saved.getContent());

        UserDTO senderDto = new UserDTO();
        senderDto.setId(sender.getId());
        senderDto.setUsername(sender.getUsername());
        senderDto.setFirstname(sender.getFirstname());
        senderDto.setLastname(sender.getLastname());
        senderDto.setRole(sender.getRole());
        senderDto.setDepartment(sender.getDepartment());

        UserDTO receiverDto = new UserDTO();
        receiverDto.setId(receiver.getId());
        receiverDto.setUsername(receiver.getUsername());
        receiverDto.setFirstname(receiver.getFirstname());
        receiverDto.setLastname(receiver.getLastname());
        receiverDto.setRole(receiver.getRole());
        receiverDto.setDepartment(receiver.getDepartment());

        dto.setSender(senderDto);
        dto.setReceiver(receiverDto);

        return dto;
    }


    @GetMapping("/conversation")
    @ResponseBody
    public List<ChatMessage> getConversation(@RequestParam Integer user1,
                                             @RequestParam Integer user2) {
        List<ChatMessage> chatMessages = chatMessageRepository.findConversation(user1, user2);
        return chatMessages;
    }


    @GetMapping("/inbox/{receiverId}")
    public List<ChatMessage> getMessagesForUser(@PathVariable Integer receiverId) {
        return chatMessageService.getMessagesForUser(receiverId);
    }


}
