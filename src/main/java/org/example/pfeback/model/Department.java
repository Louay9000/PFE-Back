package org.example.pfeback.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Table(name="Department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String departmentName;

    private String departmentDescription;

    private Long departmentCapacity;





}
