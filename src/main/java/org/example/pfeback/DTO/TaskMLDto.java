package org.example.pfeback.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskMLDto {

    private Long taskWeight;
    private Long taskStartValue;
    private Long taskDoneValue;
    private Long taskDuration;
}
