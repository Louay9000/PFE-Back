package org.example.pfeback.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pfeback.exceptions.BusinessException;
import org.example.pfeback.exceptions.ManagerAlreadyExistsException;
import org.example.pfeback.exceptions.CapacityExceededException;
import org.example.pfeback.model.*;
import org.example.pfeback.repository.DepartmentRepository;
import org.example.pfeback.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class AuthenticationService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;





    public AuthenticationService(UserRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 DepartmentRepository departmentRepository)
 {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.departmentRepository=departmentRepository;

    }



    public AuthenticationResponse register(User request, Long departmentId)  {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // Vérifie si un manager existe déjà dans le département
        if (request.getRole() == Role.MANAGER &&
                repository.existsByDepartmentIdAndRole(departmentId, Role.MANAGER)) {
            throw new ManagerAlreadyExistsException("Ce département a déjà un manager.");
        }



        // Vérifie que la capacité est suffisante
        else if (department.getDepartmentCapacity() <= 0) {
            throw new CapacityExceededException("La capacité maximale du département est atteinte.");
        }

        // Création de l'utilisateur
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setDepartment(department);

        // Décrémente la capacité et sauvegarde
        department.setDepartmentCapacity(department.getDepartmentCapacity() - 1);
        departmentRepository.save(department);

        // Sauvegarde de l'utilisateur
        user = repository.save(user);

        // Génération des tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(accessToken, refreshToken);
    }


    public List<User> getEmployeesByDepartment(Long departmentId) {
        return repository.findByDepartmentIdAndRole(departmentId, Role.EMPLOYEE);
    }


    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(accessToken,refreshToken);

    }




    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response){

        //extract the token from authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);

        }
        String token = authHeader.substring(7);

        //extract username from token
        String username = jwtService.extractUsername(token);

        //check if the user is in Database
        User user = repository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found "));

        //now check if refresh token is valid

        if(jwtService.isValidRefreshToken(token, user)){
            //generate the token
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return new ResponseEntity(new AuthenticationResponse(accessToken,refreshToken),HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }


    public void deleteUser(Integer id) {

        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Department department = user.getDepartment();

            // ✅ Incrémenter la capacité du département
            if (department != null) {
                department.setDepartmentCapacity(department.getDepartmentCapacity() + 1);
                departmentRepository.save(department);
            }

            repository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }



    public User updateUser(Integer id, User updatedUser) {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new BusinessException("Utilisateur introuvable avec l'identifiant : " + id);
        }

        User existingUser = optionalUser.get();

        Department oldDepartment = existingUser.getDepartment();
        Department newDepartment = updatedUser.getDepartment();

        Long newDeptId = newDepartment != null ? newDepartment.getId() : null;
        Department refreshedOldDepartment = oldDepartment != null ? departmentRepository.findById(oldDepartment.getId()).orElse(null) : null;
        Department refreshedNewDepartment = newDeptId != null ? departmentRepository.findById(newDeptId).orElse(null) : null;

        boolean departmentChanged = refreshedOldDepartment != null && refreshedNewDepartment != null &&
                !refreshedOldDepartment.getId().equals(refreshedNewDepartment.getId());

        // ✅ Cas : département plein
        if ((departmentChanged || oldDepartment == null) && refreshedNewDepartment.getDepartmentCapacity() <= 0) {
            throw new BusinessException("Le département '" + refreshedNewDepartment.getDepartmentName() + "' est plein.");
        }

        if (departmentChanged) {
            refreshedOldDepartment.setDepartmentCapacity(refreshedOldDepartment.getDepartmentCapacity() + 1);
            refreshedNewDepartment.setDepartmentCapacity(refreshedNewDepartment.getDepartmentCapacity() - 1);

            departmentRepository.save(refreshedOldDepartment);
            departmentRepository.save(refreshedNewDepartment);

            existingUser.setDepartment(refreshedNewDepartment);
        }

        // ✅ Cas : promotion vers manager
        boolean becomingManager = updatedUser.getRole() == Role.MANAGER && existingUser.getRole() != Role.MANAGER;
        if (becomingManager) {
            List<User> currentManagers = repository.findByDepartmentIdAndRole(refreshedNewDepartment.getId(), Role.MANAGER);

            if (!currentManagers.isEmpty()) {
                User currentManager = currentManagers.get(0);

                if (!currentManager.getDepartment().getId().equals(refreshedNewDepartment.getId())) {
                    throw new BusinessException("Impossible de promouvoir cet utilisateur en manager : il doit appartenir au même département.");
                }

                currentManager.setRole(Role.EMPLOYEE);
                repository.save(currentManager);
            }
        }

        existingUser.setFirstname(updatedUser.getFirstname());
        existingUser.setLastname(updatedUser.getLastname());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());

        if (!updatedUser.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return repository.save(existingUser);
    }


    public List<User> getAllUsers(Role role, Integer userId) {
        Optional<User> user = repository.findById(userId);
        if (role == Role.ADMIN) {
            // ADMIN : voit tous les utilisateurs triés par nom de département
            return repository.findAll(Sort.by(Sort.Direction.ASC, "department.departmentName"));
        }

        if (role == Role.MANAGER) {
            // MANAGER : voit uniquement les utilisateurs de son département
            return repository.findByDepartmentId(user.get().getDepartment().getId());
        }

        // EMPLOYEE ou rôle non reconnu : retour vide
        return List.of();
    }



    public String getDepartmentNameByUserId(Integer userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent() && user.get().getDepartment() != null) {
            return user.get().getDepartment().getDepartmentName();
        }
        return null;
    }


    public Integer getImageIdByUserId(Integer userId) {
        return repository.findImageIdByUserId(userId);
    }

    public Long getDepartmentIdByUserId(Integer userId) {
        return repository.getDepartmentIdByUserId(userId);
    }

    public String getEmailByUserId(Integer userId) {
        return repository.getEmailByUserId(userId);
    }




}
