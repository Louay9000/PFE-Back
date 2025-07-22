package org.example.pfeback.service;

import jakarta.persistence.PersistenceContext;
import org.example.pfeback.model.Objective;
import org.example.pfeback.repository.ObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ObjectiveService {
@Autowired
    private ObjectiveRepository objectiveRepository;


public List<Objective> getAllObjectives() {
    return objectiveRepository.findAll();
}


    public Objective createObjective(Objective objective) {
    return objectiveRepository.save(objective);

    }



    public void deleteObjective(Long id) {
        if (objectiveRepository.existsById(id)) {
            objectiveRepository.deleteById(id);
        } else {
            throw new RuntimeException("Objective not found with id: " + id);
        }
    }



    public Objective updateObjective(Long id, Objective updatedObjective) {
        Optional<Objective> optionalObjective = objectiveRepository.findById(id);

        if (optionalObjective.isPresent()) {
            Objective existingObjective = optionalObjective.get();
            existingObjective.setObjectiveTitle(updatedObjective.getObjectiveTitle());
            existingObjective.setObjectiveDescription(updatedObjective.getObjectiveDescription());
            existingObjective.setObjectiveStatus(updatedObjective.getObjectiveStatus());
            existingObjective.setObjectiveScore(updatedObjective.getObjectiveScore());
            existingObjective.setObjectiveProgress(updatedObjective.getObjectiveProgress());

            return objectiveRepository.save(existingObjective);
        } else {
            throw new RuntimeException("Objective not found with id: " + id);
        }
    }










}
