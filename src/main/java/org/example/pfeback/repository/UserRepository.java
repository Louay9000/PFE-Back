package org.example.pfeback.repository;

import org.example.pfeback.model.Department;
import org.example.pfeback.model.Role;
import org.example.pfeback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByDepartmentIdAndRole(Long departmentId, Role role);

    boolean existsByDepartmentId(Long departmentId);

    boolean existsByDepartmentAndRole(Department department, Role role);

    List<User> findByDepartmentIdAndRole(Long departmentId, Role role);



}
