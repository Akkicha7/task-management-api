package com.taskproject.service;

import com.taskproject.dto.AuthResponse;
import com.taskproject.dto.LoginRequest;
import com.taskproject.dto.RegisterRequest;
import com.taskproject.entity.User;
import com.taskproject.enums.Role;
import com.taskproject.repository.UserRepository;
import com.taskproject.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service handling registration and login logic.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /** Register a new user – throws RuntimeException if email already exists. */
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = User.builder()
                .name(request.getFirstName() + " " + request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
    }

    /** Authenticate a user and return a JWT wrapped in {@link AuthResponse}. */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Load the full User entity from the repository (principal is a UserDetails instance)
        User persisted = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found after authentication"));
        String token = jwtService.generateToken(persisted);
        Instant expiresAt = Instant.now().plusMillis(jwtService.getJwtExpirationMs());
        return new AuthResponse(token, expiresAt);
    }
}
