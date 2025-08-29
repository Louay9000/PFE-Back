package org.example.pfeback.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Table(name="Okr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Okr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyindicatorTitle;

    private String keyindicatorDescription;

    private Long targetValue ;

    private Long reachedValue;

    private Long okrWeight;

    private Long okrProgression;

    @ManyToOne
    @JoinColumn(name="objective_id")
    private Objective objective;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;


}
