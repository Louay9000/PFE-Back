package org.example.pfeback.controller;



import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.example.pfeback.model.Task;
import org.example.pfeback.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {


    private final TaskRepository taskRepository;

    @GetMapping(value = "/tasks-lite", produces = "text/csv; charset=UTF-8")
    public void exportTasksLite(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=tasks-lite.csv");

        // (Optionnel) BOM pour Excel
        response.getOutputStream().write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});

        List<Task> tasks = taskRepository.findAll();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("taskWeight","taskStartValue","taskDoneValue","taskState","userId","okrId")
                .build();

        try (OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
             CSVPrinter csv = new CSVPrinter(osw, format)) {

            for (Task t : tasks) {
                Integer userId = (t.getUser() != null) ? t.getUser().getId() : null;
                Long okrId  = (t.getOkr()  != null) ? t.getOkr().getId()  : null;

                csv.printRecord(
                        nullToEmpty(t.getTaskWeight()),
                        nullToEmpty(t.getTaskStartValue()),
                        nullToEmpty(t.getTaskDoneValue()),
                        t.getTaskState() != null ? t.getTaskState().name() : "",
                        nullToEmpty(userId),
                        nullToEmpty(okrId)
                );
            }
        }
    }

    private String nullToEmpty(Object v) {
        return v == null ? "" : v.toString();
    }

}
