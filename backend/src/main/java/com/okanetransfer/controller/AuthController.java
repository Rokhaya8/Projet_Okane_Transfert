package com.okanetransfer.controller;

import com.okanetransfer.dto.LoginRequest;
import com.okanetransfer.dto.LoginResponse;
import com.okanetransfer.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // TEMPORAIRE — pour générer un hash de mot de passe. À SUPPRIMER après les tests.
    @GetMapping("/hash")
    public ResponseEntity<String> hash(@RequestParam String password) {
        return ResponseEntity.ok(passwordEncoder.encode(password));
    }
}