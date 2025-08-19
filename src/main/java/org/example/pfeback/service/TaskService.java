package org.example.pfeback.service;


import lombok.RequiredArgsConstructor;
import org.example.pfeback.DTO.TaskDTO;
import org.example.pfeback.DTO.UserDTO;
import org.example.pfeback.model.*;
import org.example.pfeback.repository.ObjectiveRepository;
import org.example.pfeback.repository.OkrRepository;
import org.example.pfeback.repository.TaskRepository;
import org.example.pfeback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private OkrRepository okrRepository;
    @Autowired
    private UserRepository userRepository;


    public List<Task> getAllTasks(Role role, Integer userId) {
        Optional<User> connectedUserOptional = userRepository.findById(userId);

        if (connectedUserOptional.isEmpty()) {
            return new ArrayList<>(); // ou tu peux lever une exception personnalisée
        }

        User connectedUser = connectedUserOptional.get();

        // Vérification du rôle
        if (role == Role.ADMIN) {
            return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "taskWeight"));
        } else if (role == Role.MANAGER) {
            Long departmentId = connectedUser.getDepartment().getId(); // Long ici ✅

            return taskRepository.findByUserRoleAndUserDepartmentId(
                    Role.EMPLOYEE, departmentId, Sort.by(Sort.Direction.DESC, "taskWeight"));
        }

        // EMPLOYEE ou autre : retourne ses propres tâches (optionnel)
        return taskRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "taskWeight"));
    }



    public Task createTask(Task task , Long okrId , Integer userId) {
        Okr okr = okrRepository.findById(okrId)
                .orElseThrow(() -> new RuntimeException("Okr not found"));

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(user);
        task.setOkr(okr);
        return taskRepository.save(task);
    }

    public Task createTaskAndAssign(Task task, Long departmentId, Integer userId) {
        List<Long> okrIds = okrRepository.findOkrIdsByDepartmentId(departmentId);
        if (okrIds == null || okrIds.isEmpty()) {
            throw new RuntimeException("No OKR found for department ID: " + departmentId);
        }
        Long okrId = okrIds.get(0);
        return createTask(task, okrId, userId);
    }




    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new RuntimeException("Task not found with id: " + id);
        }
    }



    public TaskDTO updateTask(Long id, TaskDTO taskDTO, Long okrId, Integer userId) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // Récupération des entités nécessaires
        Okr okr = okrRepository.findById(okrId)
                .orElseThrow(() -> new RuntimeException("Okr not found with id: " + okrId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Mise à jour des champs
        existingTask.setTaskTitle(taskDTO.getTaskTitle());
        existingTask.setTaskDescription(taskDTO.getTaskDescription());
        existingTask.setTaskState(Status.valueOf(taskDTO.getTaskState()));
        existingTask.setTaskStartValue(taskDTO.getTaskStartValue());
        existingTask.setTaskDoneValue(taskDTO.getTaskDoneValue());
        existingTask.setTaskWeight(taskDTO.getTaskWeight());
        existingTask.setOkr(okr);
        existingTask.setUser(user);

        Task savedTask = taskRepository.save(existingTask);

        return convertToTaskDTO(savedTask); // Tu renvoies un DTO propre
    }




    public Task convertToTaskEntity(TaskDTO dto, Okr okr, User user) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTaskTitle(dto.getTaskTitle());
        task.setTaskDescription(dto.getTaskDescription());
        task.setTaskState(Status.valueOf(dto.getTaskState()));
        task.setTaskStartValue(dto.getTaskStartValue());
        task.setTaskDoneValue(dto.getTaskDoneValue());
        task.setTaskWeight(dto.getTaskWeight());
        task.setOkr(okr);
        task.setUser(user);
        return task;
    }



    public TaskDTO convertToTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTaskTitle(task.getTaskTitle());
        dto.setTaskDescription(task.getTaskDescription());
        dto.setTaskState(task.getTaskState().name());
        dto.setTaskStartValue(task.getTaskStartValue());
        dto.setTaskDoneValue(task.getTaskDoneValue());
        dto.setTaskWeight(task.getTaskWeight());
        dto.setOkrId(task.getOkr().getId());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(task.getUser().getId());
        userDTO.setFirstname(task.getUser().getFirstname());
        userDTO.setLastname(task.getUser().getLastname());
        userDTO.setUsername(task.getUser().getUsername());
        userDTO.setRole(task.getUser().getRole());
        userDTO.setDepartment(task.getUser().getDepartment());

        dto.setUser(userDTO);

        return dto;
    }


    public List<Task> getTasksByUserId(Integer userId) {
        return taskRepository.findTasksByUserIdOrderByWeightDesc(userId);
    }

    public Map<Status, Long> getObjectiveCountByStatus() {
        List<Object[]> results = taskRepository.countTasksByStatus();
        Map<Status, Long> stats = new HashMap<>();
        for (Object[] row : results) {
            Status status = (Status) row[0];
            Long count = (Long) row[1];
            stats.put(status, count);
        }
        return stats;
    }

    public User getUserWithMostReachedTasks() {
        List<Object[]> result = taskRepository.findTopUsersByReachedTasks();
        if (!result.isEmpty()) {
            return (User) result.get(0)[0];
        }
        return null;
    }

    public List<Task> getTasksByDepartmentId(Long departmentId) {
        return taskRepository.findByUser_Department_Id(departmentId);
    }

    public Map<String, Long> getTaskStatusCountByUserId(Long userId) {
        List<Object[]> results = taskRepository.countTaskStatusByUserId(userId);
        Map<String, Long> statusCount = new HashMap<>();

        for (Object[] row : results) {
            String status = row[0].toString(); // Status enum as String
            Long count = (Long) row[1];
            statusCount.put(status, count);
        }

        return statusCount;
    }

    public List<Object[]> getTasksForML() {
        return taskRepository.getTasksForML();
    }


}
