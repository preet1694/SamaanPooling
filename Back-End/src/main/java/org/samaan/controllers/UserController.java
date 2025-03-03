package org.samaan.controllers;

import org.samaan.model.User;
import org.samaan.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @PostMapping(path = "/register",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        // Check if the email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("error", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // Save user to the database
        userRepository.save(user);
        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email and password are required"));
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Please log in using Google"));
        }

        // Check password
        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }

        return ResponseEntity.ok(Map.of(
                "_id", Objects.requireNonNullElse(user.getId(), "N/A"),
                "name", Objects.requireNonNullElse(user.getName(), "N/A"),
                "role", Objects.requireNonNullElse(user.getRole(), "N/A"),
                "email", Objects.requireNonNullElse(user.getEmail(), "N/A")
        ));

    }


    // Get all users (Optional for debugging)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((User) Map.of("error", "User not found")));
    }
    @PostMapping("/getByEmail")
    public ResponseEntity<?> getUserByEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email"); // ✅ Extract email from JSON body
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((User) Collections.singletonMap("error", "User not found")));
    }
}