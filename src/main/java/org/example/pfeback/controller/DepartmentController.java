package org.example.pfeback.controller;
import org.example.pfeback.model.Department;
import org.example.pfeback.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;



    @GetMapping
    public List<Department> getAllDepartments()
    {
        return departmentService.getAllDepartments();
    }


    @GetMapping("/without-manager")
    public ResponseEntity<List<Department>> getDepartmentsWithoutManager() {
        List<Department> departments = departmentService.getDepartmentsWithoutManager();
        return ResponseEntity.ok(departments);
    }


    @PostMapping
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.createDepartment(department);
    }



    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }



    @PutMapping("/{id}")
    public Department updateDepartment(@PathVariable Long id, @RequestBody Department updatedDepartment) {
        return departmentService.updateDepartment(id, updatedDepartment);
    }


    @GetMapping("/without-okr")
    public List<Department> getDepartmentsWithoutOkr() {
        return departmentService.getDepartmentsWithoutOkr();
    }




}
