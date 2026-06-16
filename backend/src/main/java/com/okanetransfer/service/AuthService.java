package com.okanetransfer.service;

import com.okanetransfer.dto.LoginRequest;
import com.okanetransfer.dto.LoginResponse;
import com.okanetransfer.entity.User;
import com.okanetransfer.exception.BusinessException;
import com.okanetransfer.repository.UserRepository;
import com.okanetransfer.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Email ou mot de passe incorrect");
        }

        if (!user.isActive()) {
            throw new BusinessException("Compte désactivé");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(user.getId(), token, user.getFullName(), user.getRole().name());
    }
}