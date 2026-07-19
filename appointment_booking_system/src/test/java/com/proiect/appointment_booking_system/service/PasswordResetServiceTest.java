package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.PasswordResetConfirmRequest;
import com.proiect.appointment_booking_system.dto.PasswordResetRequest;
import com.proiect.appointment_booking_system.model.PasswordResetToken;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.PasswordResetTokenRepository;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private GmailApiEmailClient gmailApiEmailClient;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "senderEmail", "clinic@example.com");
        ReflectionTestUtils.setField(passwordResetService, "tokenTtlMinutes", 30L);
        ReflectionTestUtils.setField(passwordResetService, "publicBaseUrl", "https://clinic.example.com");
    }

    @Test
    void requestPasswordReset_WhenUserExists_ShouldStoreHashedTokenAndSendEmail() {
        User user = user();
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("PATIENT@example.com");

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUserIdAndUsedAtIsNull(user.getId())).thenReturn(List.of());

        passwordResetService.requestPasswordReset(request);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());

        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(emailCaptor.capture());

        String rawToken = extractResetToken(emailCaptor.getValue().getText());
        assertNotNull(rawToken);
        assertNotEquals(rawToken, tokenCaptor.getValue().getTokenHash());
        assertTrue(tokenCaptor.getValue().getExpiresAt().isAfter(tokenCaptor.getValue().getCreatedAt()));
    }

    @Test
    void requestPasswordReset_WhenUserDoesNotExist_ShouldNotCreateTokenOrSendEmail() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("missing@example.com");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset(request);

        verify(passwordResetTokenRepository, never()).save(any());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verify(gmailApiEmailClient, never()).sendEmail(any(), any(), any(), any());
    }

    @Test
    void confirmPasswordReset_WhenTokenIsValid_ShouldChangePasswordAndConsumeTokens() throws Exception {
        String rawToken = "reset-token";
        User user = user();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hash(rawToken));
        resetToken.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(2));
        resetToken.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(28));

        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest();
        request.setToken(rawToken);
        request.setNewPassword("NewPassword123!");

        when(passwordResetTokenRepository.findByTokenHashAndUsedAtIsNull(hash(rawToken)))
                .thenReturn(Optional.of(resetToken));
        when(passwordResetTokenRepository.findByUserIdAndUsedAtIsNull(user.getId()))
                .thenReturn(List.of(resetToken));
        when(passwordEncoder.encode("NewPassword123!")).thenReturn("encoded-new-password");

        passwordResetService.confirmPasswordReset(request);

        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).saveAll(List.of(resetToken));
        assertTrue(resetToken.getUsedAt().isBefore(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1)));
        assertTrue(user.getPassword().equals("encoded-new-password"));
    }

    private User user() {
        User user = new User();
        user.setId(20L);
        user.setName("Patient Test");
        user.setEmail("patient@example.com");
        user.setPassword("old-password");
        user.setPhoneNumber("0712345678");
        return user;
    }

    private String extractResetToken(String emailText) {
        Matcher matcher = Pattern.compile("resetToken=([^\\s]+)").matcher(emailText);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String hash(String token) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
