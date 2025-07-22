package org.example.pfeback.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pfeback.DTO.UserDTO;
import org.example.pfeback.exceptions.CapacityExceededException;
import org.example.pfeback.exceptions.ManagerAlreadyExistsException;
import org.example.pfeback.model.AuthenticationResponse;
import org.example.pfeback.model.Department;
import org.example.pfeback.model.User;
import org.example.pfeback.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class AuthenticationController {


    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authService = authenticationService;
    }

    @PostMapping("/register/{departmentId}")
    public ResponseEntity<AuthenticationResponse> register( @RequestBody User request,@PathVariable Long departmentId) {
        return ResponseEntity.ok(authService.register(request, departmentId));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request){
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response){
        return authService.refreshToken(request,response);
    }


    @GetMapping
    public List<User> getAllUsers()
    {
        return authService.getAllUsers();
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        authService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public User updatedUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        return authService.updateUser(id, updatedUser);
    }

    @GetMapping("/departments/{departmentId}/employees")
    public ResponseEntity<List<User>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<User> employees = authService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody UserDTO updatedUserDto) {
        User updatedUser = new User();
        updatedUser.setId(updatedUserDto.getId());
        updatedUser.setFirstname(updatedUserDto.getFirstname());
        updatedUser.setLastname(updatedUserDto.getLastname());
        updatedUser.setUsername(updatedUserDto.getUsername());
        updatedUser.setPassword(updatedUserDto.getPassword());
        updatedUser.setRole(updatedUserDto.getRole());
        updatedUser.setDepartment(updatedUserDto.getDepartment());

        User result = authService.updateUser(id, updatedUser);
        return ResponseEntity.ok(result);
    }


    @ExceptionHandler(ManagerAlreadyExistsException.class)
    public ResponseEntity<String> handleManagerExists(ManagerAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(CapacityExceededException.class)
    public ResponseEntity<String> handleCapacityExceeded(CapacityExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
