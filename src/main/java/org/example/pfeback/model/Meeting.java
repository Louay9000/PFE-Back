package org.example.pfeback.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Table(name="Meeting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String link;

    private LocalDateTime startTime;
    private LocalDateTime endTime;



}
