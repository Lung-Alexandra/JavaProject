package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.PasswordResetConfirmRequest;
import com.proiect.appointment_booking_system.dto.PasswordResetRequest;
import com.proiect.appointment_booking_system.model.PasswordResetToken;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.PasswordResetTokenRepository;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
public class PasswordResetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_BYTES = 32;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private GmailApiEmailClient gmailApiEmailClient;

    @Value("${notifications.email.from:${spring.mail.username:}}")
    private String senderEmail;

    @Value("${password-reset.token.ttl-minutes:30}")
    private long tokenTtlMinutes;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        String email = normalizeEmail(request.getEmail());
        userRepository.findByEmail(email).ifPresent(this::createAndSendResetToken);
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        String tokenHash = hashToken(request.getToken());
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHashAndUsedAtIsNull(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Password reset link is invalid or expired."));

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (resetToken.getExpiresAt().isBefore(now)) {
            resetToken.setUsedAt(now);
            passwordResetTokenRepository.save(resetToken);
            throw new IllegalArgumentException("Password reset link is invalid or expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        markActiveTokensUsed(user.getId(), now);
    }

    private void createAndSendResetToken(User user) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        markActiveTokensUsed(user.getId(), now);

        String rawToken = generateToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hashToken(rawToken));
        resetToken.setCreatedAt(now);
        resetToken.setExpiresAt(now.plusMinutes(tokenTtlMinutes));
        passwordResetTokenRepository.save(resetToken);

        sendResetEmail(user, rawToken);
    }

    private void markActiveTokensUsed(Long userId, LocalDateTime usedAt) {
        List<PasswordResetToken> activeTokens = passwordResetTokenRepository.findByUserIdAndUsedAtIsNull(userId);
        activeTokens.forEach(token -> token.setUsedAt(usedAt));
        passwordResetTokenRepository.saveAll(activeTokens);
    }

    private void sendResetEmail(User user, String rawToken) {
        String resetLink = UriComponentsBuilder
                .fromUriString(publicBaseUrl)
                .queryParam("resetToken", rawToken)
                .build()
                .toUriString();
        String subject = "Reset your password";
        String body = String.format(
                "Hi %s,%n%nUse this link to reset your password:%n%s%n%nThis link expires in %d minutes.%nIf you did not request this, ignore this email.",
                user.getName(),
                resetLink,
                tokenTtlMinutes);

        if (sendWithSmtp(user.getEmail(), subject, body)) {
            return;
        }
        sendWithGmailApi(user, subject, body);
    }

    private boolean sendWithSmtp(String recipient, String subject, String body) {
        if (mailSender == null) {
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        if (senderEmail != null && !senderEmail.isBlank()) {
            message.setFrom(senderEmail);
        }
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            LOGGER.warn("SMTP failed while sending password reset email to {}. Trying Gmail API fallback.", recipient, exception);
            return false;
        }
    }

    private void sendWithGmailApi(User user, String subject, String body) {
        if (!gmailApiEmailClient.isConfigured()) {
            LOGGER.warn("Password reset token was created for {}, but no email provider is configured.", user.getEmail());
            return;
        }
        try {
            String messageId = gmailApiEmailClient.sendEmail(user.getEmail(), user.getName(), subject, body);
            LOGGER.info("Gmail API accepted password reset email for {} with message id {}.",
                    user.getEmail(),
                    messageId != null ? messageId : "-");
        } catch (Exception exception) {
            LOGGER.warn("Gmail API failed while sending password reset email to {}.", user.getEmail(), exception);
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.trim().getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
