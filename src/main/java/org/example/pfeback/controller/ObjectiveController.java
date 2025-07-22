package org.example.pfeback.controller;


import org.example.pfeback.model.Objective;
import org.example.pfeback.repository.ObjectiveRepository;
import org.example.pfeback.service.ObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/objectives")
@CrossOrigin(origins = "*")

public class ObjectiveController {

    @Autowired
    ObjectiveService objectiveService;

    @GetMapping
    public List<Objective> getAllObjectives()
    {
        return objectiveService.getAllObjectives();
    }



    @PostMapping
    public Objective createObjective(@RequestBody Objective objective) {
        return objectiveService.createObjective(objective);
    }



    @DeleteMapping("/{id}")
    public void deleteObjective(@PathVariable Long id) {
        objectiveService.deleteObjective(id);
    }



    @PutMapping("/{id}")
    public Objective updateObjective(@PathVariable Long id, @RequestBody Objective updatedObjective) {
        return objectiveService.updateObjective(id, updatedObjective);
    }




}
