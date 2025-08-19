package org.example.pfeback.controller;

import org.example.pfeback.DTO.TaskMLDto;
import org.example.pfeback.service.FlaskService;

import org.example.pfeback.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mlservice")  // NE PAS utiliser /tasks
public class MLTestController {

    private final FlaskService flaskService;

    private  final TaskService taskService;

    public MLTestController(FlaskService flaskService, TaskService taskService) {
        this.flaskService = flaskService;
        this.taskService =  taskService;
    }

    @GetMapping("/test")
    public Map<String, Object> testFlask() {
        // Exemple de valeurs de test
        return flaskService.getTaskProbabilities(4L, 0L, 2L);
    }

    // ✅ Nouvelle méthode POST pour Angular
    @CrossOrigin(origins = "http://localhost:4200") // autorise Angular
    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(@RequestBody Map<String, Long> task) {
        Long weight = task.get("taskWeight");
        Long start = task.get("taskStartValue");
        Long done = task.get("taskDoneValue");

        Map<String, Object> proba = flaskService.getTaskProbabilities(weight, start, done);
        return ResponseEntity.ok(proba);
    }


    @GetMapping("/predicttasks")
    public List<TaskMLDto> getTasksForML() {
        List<Object[]> rows = taskService.getTasksForML();

        List<TaskMLDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            TaskMLDto dto = new TaskMLDto(
                    ((Number) row[0]).longValue(), // taskWeight
                    ((Number) row[1]).longValue(), // taskStartValue
                    ((Number) row[2]).longValue(), // taskDoneValue
                    ((Number) row[3]).longValue()  // taskDuration
            );
            result.add(dto);
        }
        return result;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/predict-duration")
    public ResponseEntity<Map<String, Object>> predictDuration(@RequestBody Map<String, Long> task) {
        Long weight = task.get("taskWeight");
        Long start = task.get("taskStartValue");
        Long done = task.get("taskDoneValue");

        Map<String, Object> result = flaskService.getTaskDuration(weight, start, done);
        return ResponseEntity.ok(result);
    }
}
