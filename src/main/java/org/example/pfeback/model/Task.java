package org.example.pfeback.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Table(name="Task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskTitle;

    private String taskDescription;

    @Enumerated(value =EnumType.STRING)
    private Status taskState;

    private Long taskStartValue;

    private Long taskDoneValue;

    private Long taskWeight;

    @ManyToOne
    @JoinColumn(name = "okr_id")
    private Okr okr;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;


}
