package org.example.pfeback.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {

    private Long id;
    private String taskTitle;
    private String taskDescription;
    private String taskState;
    private Long taskStartValue;
    private Long taskDoneValue;
    private Long taskWeight;
    private Long okrId; // On envoie juste l'ID
    private UserDTO user; // Le DTO léger de l'utilisateur
}
