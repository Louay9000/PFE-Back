package org.example.pfeback.service;


import org.example.pfeback.exceptions.UserAlreadyExistsInDepartment;
import org.example.pfeback.model.Department;
import org.example.pfeback.model.Role;
import org.example.pfeback.repository.DepartmentRepository;
import org.example.pfeback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;


    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }



    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }


    public List<Department> getDepartmentsWithoutManager() {
        List<Department> allDepartments = departmentRepository.findAll();
        return allDepartments.stream()
                .filter(dept -> !userRepository.existsByDepartmentAndRole(dept, Role.MANAGER))
                .toList();
    }




    public Department createDepartment(Department department) {
        if(departmentRepository.existsByDepartmentNameIgnoreCase(department.getDepartmentName().toUpperCase())) {
            throw new IllegalArgumentException("Un département avec ce nom existe déjà.");
        }
        department.setDepartmentName(department.getDepartmentName().toUpperCase());
        return departmentRepository.save(department);
    }




    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }

        // Vérifier si le département contient des utilisateurs
        else if (userRepository.existsByDepartmentId(id)) {
            throw new UserAlreadyExistsInDepartment("User already exists in department with id: ");
        }
        else {departmentRepository.deleteById(id);}
        
    }






    public Department updateDepartment(Long id, Department updatedDepartment) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);

        if (optionalDepartment.isPresent()) {
            Department existingDepartment = optionalDepartment.get();

            existingDepartment.setDepartmentName(updatedDepartment.getDepartmentName());
            existingDepartment.setDepartmentDescription(updatedDepartment.getDepartmentDescription());
            existingDepartment.setDepartmentCapacity(updatedDepartment.getDepartmentCapacity());


            return departmentRepository.save(existingDepartment);
        } else {
            throw new RuntimeException("Department not found with id: " + id);
        }
    }


    public List<Department> getDepartmentsWithoutOkr() {
        return departmentRepository.findDepartmentsWithoutOkr();
    }



}
