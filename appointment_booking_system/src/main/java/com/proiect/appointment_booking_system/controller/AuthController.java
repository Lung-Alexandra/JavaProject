package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.AuthRequest;
import com.proiect.appointment_booking_system.dto.AuthResponse;
import com.proiect.appointment_booking_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/patient/login")
    public ResponseEntity<AuthResponse> loginPatient(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(authService.loginPatient(request));
    }

    @PostMapping("/doctor/login")
    public ResponseEntity<AuthResponse> loginDoctor(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(authService.loginDoctor(request));
    }
}
