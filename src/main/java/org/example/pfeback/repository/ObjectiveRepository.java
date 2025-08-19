package org.example.pfeback.repository;

import org.example.pfeback.model.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {

    @Query("SELECT o.objectiveStatus, COUNT(o) FROM Objective o GROUP BY o.objectiveStatus")
    List<Object[]> countObjectivesByStatus();

}
