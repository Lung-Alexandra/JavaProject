package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.AuthRequest;
import com.proiect.appointment_booking_system.dto.AuthResponse;
import com.proiect.appointment_booking_system.enums.Role;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.UserRepository;
import com.proiect.appointment_booking_system.security.JwtService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(AuthRequest authRequest) {
        User user = authenticate(authRequest);
        return buildResponse(user);
    }

    public AuthResponse loginPatient(AuthRequest authRequest) {
        User user = authenticate(authRequest);
        ensureRole(user, Role.PATIENT);
        return buildResponse(user);
    }

    public AuthResponse loginDoctor(AuthRequest authRequest) {
        User user = authenticate(authRequest);
        ensureRole(user, Role.DOCTOR);
        return buildResponse(user);
    }

    private User authenticate(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!isPasswordValid(authRequest.getPassword(), user)) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return user;
    }

    private void ensureRole(User user, Role expectedRole) {
        if (user.getRole() != expectedRole) {
            throw new AccessDeniedException("This account is not allowed on this login endpoint.");
        }
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name());
    }

    private boolean isPasswordValid(String rawPassword, User user) {
        String storedPassword = user.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        if (rawPassword.equals(storedPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
