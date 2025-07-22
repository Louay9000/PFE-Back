package org.example.pfeback.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name="Objective")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Objective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objectiveTitle;

    private String objectiveDescription;

    @Enumerated(value =EnumType.STRING)
    private Status objectiveStatus ;

    private Long objectiveScore ;

    private Long objectiveProgress ;

}
