package org.example.pfeback.service;

import jakarta.persistence.PersistenceContext;
import org.example.pfeback.model.Objective;
import org.example.pfeback.model.Okr;
import org.example.pfeback.model.Status;
import org.example.pfeback.repository.ObjectiveRepository;
import org.example.pfeback.repository.OkrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class ObjectiveService {
@Autowired
    private ObjectiveRepository objectiveRepository;
@Autowired
    private OkrRepository okrRepository;



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


    public Map<Status, Long> getObjectiveCountByStatus() {
        List<Object[]> results = objectiveRepository.countObjectivesByStatus();
        Map<Status, Long> stats = new HashMap<>();
        for (Object[] row : results) {
            Status status = (Status) row[0];
            Long count = (Long) row[1];
            stats.put(status, count);
        }
        return stats;
    }

    public void updateObjectiveStatusIfOkrsReached(Long objectiveId) {
        Optional<Objective> optionalObjective = objectiveRepository.findById(objectiveId);

        if (optionalObjective.isPresent()) {
            Objective objective = optionalObjective.get();

            List<Okr> okrs = okrRepository.findByObjectiveId(objectiveId);

            boolean allReached = okrs.stream()
                    .allMatch(okr -> okr.getReachedValue() != null &&
                            okr.getTargetValue() != null &&
                            okr.getReachedValue().equals(okr.getTargetValue()));

            if (allReached && !okrs.isEmpty()) {
                objective.setObjectiveStatus(Status.REACHED);
                objectiveRepository.save(objective);
            }
        } else {
            throw new RuntimeException("Objective not found with id: " + objectiveId);
        }
    }



}
