package com.caseapp.socialengine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caseapp.socialengine.service.JwtService;

import lombok.Data;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // For right now, we will hardcode the admin credentials.
        // Once this works, we will link it to your PostgreSQL database!
        if ("admin".equals(request.getUsername()) && "password123".equals(request.getPassword())) {
            String token = jwtService.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials!");
        }
    }
}

// Quick helper classes to format the JSON data
@Data
class LoginRequest {
    private String username;
    private String password;
}

@Data
class AuthResponse {
    private String token;
    public AuthResponse(String token) { this.token = token; }
}