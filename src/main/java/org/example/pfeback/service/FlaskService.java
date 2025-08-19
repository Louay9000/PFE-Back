package org.example.pfeback.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FlaskService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String flaskUrl = "http://127.0.0.1:5000/predict";

    private final String flaskDurationUrl = "http://127.0.0.1:5001/predict";

    public Map<String, Object> getTaskProbabilities(Long taskWeight, Long taskStartValue, Long taskDoneValue) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("taskWeight", taskWeight);
        requestBody.put("taskStartValue", taskStartValue);
        requestBody.put("taskDoneValue", taskDoneValue);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, request, Map.class);
        return response.getBody();
    }

    public Map<String, Object> getTaskDuration(Long taskWeight, Long taskStartValue, Long taskDoneValue) {
        Map<String, Long> body = Map.of(
                "taskWeight", taskWeight,
                "taskStartValue", taskStartValue,
                "taskDoneValue", taskDoneValue
        );

        return restTemplate.postForObject(flaskDurationUrl, body, Map.class);
    }
}
