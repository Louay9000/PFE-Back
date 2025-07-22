package org.example.pfeback.repository;

import org.example.pfeback.model.Department;
import org.example.pfeback.model.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByDepartmentNameIgnoreCase(String departmentName);


    @Query("SELECT d FROM Department d WHERE d.id NOT IN (SELECT o.department.id FROM Okr o WHERE o.department IS NOT NULL)")
    List<Department> findDepartmentsWithoutOkr();
}
