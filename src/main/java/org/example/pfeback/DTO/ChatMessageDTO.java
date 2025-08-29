package org.example.pfeback.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private Long id;
    private String content;
    private UserDTO sender;
    private UserDTO receiver;
}
