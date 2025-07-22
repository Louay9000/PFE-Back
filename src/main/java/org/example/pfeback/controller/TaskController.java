package org.example.pfeback.controller;


import org.example.pfeback.DTO.TaskDTO;
import org.example.pfeback.model.Task;
import org.example.pfeback.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks()
    {return taskService.getAllTasks();
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


}
