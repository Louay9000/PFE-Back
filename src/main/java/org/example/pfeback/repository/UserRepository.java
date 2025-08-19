package org.example.pfeback.repository;

import org.example.pfeback.model.Department;
import org.example.pfeback.model.Role;
import org.example.pfeback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository  extends JpaRepository<User,Integer> {

    Optional<User> findByUsername(String username);

    List<User> findByDepartmentId(Long departmentId);

    boolean existsByDepartmentIdAndRole(Long departmentId, Role role);

    boolean existsByDepartmentId(Long departmentId);

    boolean existsByDepartmentAndRole(Department department, Role role);

    List<User> findByDepartmentIdAndRole(Long departmentId, Role role);

    @Query("SELECT u.image.id FROM User u WHERE u.id = :userId")
    Integer findImageIdByUserId(@Param("userId") Integer userId);

    @Query("SELECT u.department.id FROM User u WHERE u.id = :userId")
    Long getDepartmentIdByUserId(@Param("userId") Integer userId);

    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    String getEmailByUserId(Integer userId);



}
