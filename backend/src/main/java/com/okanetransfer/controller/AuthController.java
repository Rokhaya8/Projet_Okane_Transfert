package com.okanetransfer.controller;

import com.okanetransfer.dto.request.LoginRequest;
import com.okanetransfer.dto.response.LoginResponse;
import com.okanetransfer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/hash")
    public ResponseEntity<String> hash(@RequestParam String password) {
        return ResponseEntity.ok(passwordEncoder.encode(password));
    }
}
