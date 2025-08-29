package org.example.pfeback.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pfeback.DTO.UserDTO;
import org.example.pfeback.exceptions.CapacityExceededException;
import org.example.pfeback.exceptions.ManagerAlreadyExistsException;
import org.example.pfeback.model.*;
import org.example.pfeback.repository.UserRepository;
import org.example.pfeback.service.AuthenticationService;
import org.example.pfeback.service.CloudinaryService;
import org.example.pfeback.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class AuthenticationController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

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


    @GetMapping("/{role}/{userId}")
    public ResponseEntity<List<User>> getUsers(@PathVariable Role role , @PathVariable Integer userId) {
        List<User> users = authService.getAllUsers(role, userId);
        return ResponseEntity.ok(users);
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
        updatedUser.setEmail(updatedUserDto.getEmail());
        updatedUser.setPassword(updatedUserDto.getPassword());
        updatedUser.setRole(updatedUserDto.getRole());
        updatedUser.setDepartment(updatedUserDto.getDepartment());

        User result = authService.updateUser(id, updatedUser);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/{userId}/upload-image")
    public ResponseEntity<?> uploadUserImage(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile multipartFile) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé !");
            }
            BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
            if (bi == null) {
                return ResponseEntity.badRequest().body("Fichier non valide !");
            }
            // Upload sur Cloudinary
            Map result = cloudinaryService.upload(multipartFile);
            Image image = new Image(
                    (String) result.get("original_filename"),
                    (String) result.get("url"),
                    (String) result.get("public_id")
            );

            imageService.save(image); // Sauvegarder dans la base

            // Associer à l'utilisateur
            User user = optionalUser.get();
            user.setImage(image);
            userRepository.save(user);

            return ResponseEntity.ok("Image uploadée et associée avec succès !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l’upload.");
        }
    }

    @GetMapping("/{id}/department-name")
    public ResponseEntity<String> getDepartmentName(@PathVariable Integer id) {
        String deptName = authService.getDepartmentNameByUserId(id);
        if (deptName != null) {
            return ResponseEntity.ok(deptName);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/image-id")
    public ResponseEntity<Integer> getImageId(@PathVariable Integer id) {
        Integer imageId = authService.getImageIdByUserId(id);
        if (imageId != null) {
            return ResponseEntity.ok(imageId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/department-id")
    public ResponseEntity<Long> getDepartmentIdByUserId(@PathVariable Integer userId) {
        Long departmentId = authService.getDepartmentIdByUserId(userId);
        return ResponseEntity.ok(departmentId);
    }


    @GetMapping("/{userId}/email")
    public ResponseEntity<String> getEmailByUserId(@PathVariable Integer userId) {
        String email = authService.getEmailByUserId(userId);
        return ResponseEntity.ok(email);
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
