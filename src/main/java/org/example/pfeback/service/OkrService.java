package org.example.pfeback.service;


import org.example.pfeback.model.Department;
import org.example.pfeback.model.Objective;
import org.example.pfeback.model.Okr;
import org.example.pfeback.repository.DepartmentRepository;
import org.example.pfeback.repository.ObjectiveRepository;
import org.example.pfeback.repository.OkrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OkrService {
    @Autowired
    private OkrRepository okrRepository;
    @Autowired
    private ObjectiveRepository objectiveRepository;
    @Autowired
    private DepartmentRepository departmentRepository;



    public List<Okr> getAllOkrs() {
        return okrRepository.findAll(Sort.by(Sort.Direction.DESC,"okrWeight"));
    }

    public Okr createOkr(Okr okr,Long departmentId,Long objectiveId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Objective objective = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new RuntimeException("Objective not found"));

        okr.setDepartment(department);
        okr.setObjective(objective);

        return okrRepository.save(okr);
    }


    public void deleteOkr(Long id) {
        if (okrRepository.existsById(id)) {
            okrRepository.deleteById(id);
        } else {
            throw new RuntimeException("Okr not found with id: " + id);
        }
    }


    public Okr updateOkr(Long id, Okr updatedOkr, Long departmentId, Long objectiveId) {
        Okr existingOkr = okrRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Okr not found with id: " + id));

        existingOkr.setKeyindicatorTitle(updatedOkr.getKeyindicatorTitle());
        existingOkr.setKeyindicatorDescription(updatedOkr.getKeyindicatorDescription());
        existingOkr.setTargetValue(updatedOkr.getTargetValue());
        existingOkr.setReachedValue(updatedOkr.getReachedValue());
        existingOkr.setOkrWeight(updatedOkr.getOkrWeight());
        existingOkr.setOkrProgression(updatedOkr.getOkrProgression());

        // Mise à jour du département s’il est fourni
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));
            existingOkr.setDepartment(department);
        }

        // Mise à jour de l’objectif s’il est fourni
        if (objectiveId != null) {
            Objective objective = objectiveRepository.findById(objectiveId)
                    .orElseThrow(() -> new RuntimeException("Objective not found with id: " + objectiveId));
            existingOkr.setObjective(objective);
        }

        return okrRepository.save(existingOkr);
    }


}