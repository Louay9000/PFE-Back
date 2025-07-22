package org.example.pfeback.controller;

import org.example.pfeback.exceptions.BusinessException;
import org.example.pfeback.exceptions.CapacityExceededException;
import org.example.pfeback.exceptions.ManagerAlreadyExistsException;
import org.example.pfeback.exceptions.UserAlreadyExistsInDepartment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(ManagerAlreadyExistsException.class)
    public ResponseEntity<String> handleManagerExists(ManagerAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CapacityExceededException.class)
    public ResponseEntity<String> handleCapacityExceeded(CapacityExceededException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsInDepartment.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsInDepartment ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGeneral(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
