package org.example.pfeback.repository;

import org.example.pfeback.model.Department;
import org.example.pfeback.model.Objective;
import org.example.pfeback.model.Okr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OkrRepository extends JpaRepository <Okr,Long> {
    @Query("SELECT o.id FROM Okr o WHERE o.department.id = :departmentId")
    List<Long> findOkrIdsByDepartmentId(@Param("departmentId") Long departmentId);

    List<Okr> findByDepartmentId(Long departmentId);

    List<Okr> findByObjectiveId(Long objectiveId);

    boolean existsByObjectiveAndDepartment(Objective objective, Department department);



}
