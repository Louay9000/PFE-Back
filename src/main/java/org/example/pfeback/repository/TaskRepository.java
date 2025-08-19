package org.example.pfeback.repository;

import org.example.pfeback.model.Role;
import org.example.pfeback.model.Task;
import org.example.pfeback.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Integer userId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId ORDER BY t.taskWeight DESC")
    List<Task> findTasksByUserIdOrderByWeightDesc(@Param("userId") Integer userId);

    List<Task> findByUserRoleAndUserDepartmentId(Role role, Long departmentId, Sort sort);

    List<Task> findByUserId(Integer userId, Sort sort);

    @Query("SELECT t.taskState, COUNT(t) FROM Task t GROUP BY t.taskState")
    List<Object[]> countTasksByStatus();

    @Query("SELECT t.user, COUNT(t) as reachedCount " +
            "FROM Task t " +
            "WHERE t.taskState = org.example.pfeback.model.Status.REACHED " +
            "GROUP BY t.user " +
            "ORDER BY reachedCount DESC")
    List<Object[]> findTopUsersByReachedTasks();

    List<Task> findByUser_Department_Id(Long departmentId);

    @Query("SELECT t.taskState, COUNT(t) " +
            "FROM Task t " +
            "WHERE t.user.id = :userId " +
            "GROUP BY t.taskState")
    List<Object[]> countTaskStatusByUserId(Long userId);

    @Query(value = "SELECT task_weight AS taskWeight, " +
            "task_start_value AS taskStartValue, " +
            "task_done_value AS taskDoneValue, " +
            "(task_done_value - task_start_value) AS taskDuration " +
            "FROM task", nativeQuery = true)
    List<Object[]> getTasksForML();


}
