package org.example.pfeback.controller;


import org.example.pfeback.DTO.TaskDTO;
import org.example.pfeback.model.Role;
import org.example.pfeback.model.Status;
import org.example.pfeback.model.Task;
import org.example.pfeback.model.User;
import org.example.pfeback.service.FlaskService;
import org.example.pfeback.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    FlaskService flaskService;

    @GetMapping("/{role}/{userId}")
    public List<Task> getAllTasks(@PathVariable Role role , @PathVariable Integer userId) {
        return taskService.getAllTasks(role, userId);
    }


    @PostMapping("/{okrId}/{userId}")
    public Task createTask(@RequestBody Task task,@PathVariable Long okrId,@PathVariable Integer userId) {
        return taskService.createTask(task,okrId,userId);
    }


    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }


    @PutMapping("/{id}/{okrId}/{userId}")
    public TaskDTO updateTask(@PathVariable Long id,
                              @RequestBody TaskDTO taskDTO,
                              @PathVariable Long okrId,
                              @PathVariable Integer userId) {
        return taskService.updateTask(id, taskDTO, okrId, userId);
    }

    @GetMapping("/{userId}")
    public List<Task> getTasksByUser(@PathVariable Integer userId) {
        return taskService.getTasksByUserId(userId);
    }


    @PostMapping("/assign/{departmentId}/{userId}")
    public Task createTaskAndAssign(@RequestBody Task task,@PathVariable Long departmentId,@PathVariable Integer userId) {
        return taskService.createTaskAndAssign(task,departmentId,userId);
    }

    @GetMapping("/stats/status")
    public Map<Status, Long> getObjectivesCountByStatus() {
        return taskService.getObjectiveCountByStatus();
    }

    @GetMapping("/top-user-reached")
    public ResponseEntity<User> getUserWithMostReachedTasks() {
        User topUser = taskService.getUserWithMostReachedTasks();
        return topUser != null
                ? ResponseEntity.ok(topUser)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/by-department/{departmentId}")
    public List<Task> getTasksByDepartment(@PathVariable Long departmentId) {
        return taskService.getTasksByDepartmentId(departmentId);
    }

    @GetMapping("/stats-by-user/{userId}")
    public Map<String, Long> getTaskStatusCountByUser(@PathVariable Long userId) {
        return taskService.getTaskStatusCountByUserId(userId);
    }

    public TaskController(FlaskService flaskService) {
        this.flaskService = flaskService;
    }


    @GetMapping("/tasks/test-flask")
    public Map<String, Object> testFlask() {
        // Exemple de valeurs de test
        return flaskService.getTaskProbabilities(4L, 0L, 2L);
    }


}
