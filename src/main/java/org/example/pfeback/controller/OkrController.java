package org.example.pfeback.controller;

import org.example.pfeback.model.Okr;
import org.example.pfeback.service.OkrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/okrs")
@CrossOrigin(origins = "*")
public class OkrController {

    @Autowired
    private OkrService okrService;


    @GetMapping
    public List<Okr> getAllOkrs()
    {
        return okrService.getAllOkrs();
    }


    @PostMapping("/{departmentId}/{objectiveId}")
    public Okr createOkr(@RequestBody Okr okr,@PathVariable Long departmentId,@PathVariable Long objectiveId) {
        return okrService.createOkr(okr,departmentId,objectiveId);
    }

    @DeleteMapping("/{id}")
    public void deleteOkr(@PathVariable Long id) {
        okrService.deleteOkr(id);
    }

    @PutMapping("/{id}/{departmentId}/{objectiveId}")
    public Okr updateOkr(@PathVariable Long id, @RequestBody Okr updatedOkr,@PathVariable Long departmentId,@PathVariable Long objectiveId) {
        return okrService.updateOkr(id, updatedOkr, departmentId, objectiveId);
    }

    @GetMapping("/{departmentId}")
    public List<Long> getOkrIdsByDepartmentId(@PathVariable Long departmentId) {
        return okrService.getOkrIdsByDepartmentId(departmentId);
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<List<Okr>> getOkrsByDepartment(@PathVariable Long departmentId) {
        List<Okr> okrs = okrService.getOkrsByDepartment(departmentId);
        return ResponseEntity.ok(okrs);
    }


}
