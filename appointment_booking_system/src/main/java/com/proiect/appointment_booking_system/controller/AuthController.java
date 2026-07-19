package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.AuthRequest;
import com.proiect.appointment_booking_system.dto.AuthResponse;
import com.proiect.appointment_booking_system.dto.PasswordResetConfirmRequest;
import com.proiect.appointment_booking_system.dto.PasswordResetRequest;
import com.proiect.appointment_booking_system.service.AuthService;
import com.proiect.appointment_booking_system.service.PasswordResetService;
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
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
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

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok("If an account exists for that email, a password reset link has been sent.");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestBody @Valid PasswordResetConfirmRequest request) {
        passwordResetService.confirmPasswordReset(request);
        return ResponseEntity.ok("Password reset successfully.");
    }
}
