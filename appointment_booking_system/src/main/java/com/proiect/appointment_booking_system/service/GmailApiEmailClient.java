package com.proiect.appointment_booking_system.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class GmailApiEmailClient {

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GMAIL_SEND_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String fromEmail;
    private final String fromName;

    public GmailApiEmailClient(
            RestClient.Builder restClientBuilder,
            @Value("${notifications.email.gmail-api.client-id:}") String clientId,
            @Value("${notifications.email.gmail-api.client-secret:}") String clientSecret,
            @Value("${notifications.email.gmail-api.refresh-token:}") String refreshToken,
            @Value("${notifications.email.gmail-api.from-email:${notifications.email.from:}}") String fromEmail,
            @Value("${notifications.email.gmail-api.from-name:Java Appointment}") String fromName) {
        this.restClient = restClientBuilder.build();
        this.clientId = normalize(clientId);
        this.clientSecret = normalize(clientSecret);
        this.refreshToken = normalize(refreshToken);
        this.fromEmail = normalize(fromEmail);
        this.fromName = normalize(fromName);
    }

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank()
                && refreshToken != null && !refreshToken.isBlank()
                && fromEmail != null && !fromEmail.isBlank();
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String sendEmail(String recipientEmail, String recipientName, String subject, String textContent) {
        if (!isConfigured()) {
            throw new IllegalStateException("Gmail API fallback is not configured.");
        }

        String accessToken = getAccessToken();
        String rawMessage = buildRawMessage(recipientEmail, recipientName, subject, textContent);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("raw", rawMessage);

        GmailSendResponse response = restClient.post()
                .uri(GMAIL_SEND_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(GmailSendResponse.class);

        return response != null ? response.id() : null;
    }

    private String getAccessToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("grant_type", "refresh_token");

        GmailTokenResponse response = restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .body(GmailTokenResponse.class);

        if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
            throw new IllegalStateException("Gmail API did not return an access token.");
        }
        return response.accessToken();
    }

    private String buildRawMessage(String recipientEmail, String recipientName, String subject, String textContent) {
        try {
            String message = String.join("\r\n",
                    "From: " + mailbox(fromEmail, fromName),
                    "To: " + mailbox(recipientEmail, recipientName),
                    "Reply-To: " + mailbox(fromEmail, fromName),
                    "Subject: " + MimeUtility.encodeText(subject, StandardCharsets.UTF_8.name(), null),
                    "MIME-Version: 1.0",
                    "Content-Type: text/plain; charset=UTF-8",
                    "Content-Transfer-Encoding: 8bit",
                    "",
                    textContent);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(message.getBytes(StandardCharsets.UTF_8));
        } catch (AddressException | UnsupportedEncodingException exception) {
            throw new IllegalStateException("Could not build Gmail API message.", exception);
        }
    }

    private String mailbox(String email, String name) throws AddressException, UnsupportedEncodingException {
        String normalizedEmail = normalize(email);
        String normalizedName = normalize(name);
        if (normalizedName == null || normalizedName.isBlank()) {
            return new InternetAddress(normalizedEmail).toString();
        }
        return new InternetAddress(normalizedEmail, normalizedName, StandardCharsets.UTF_8.name()).toString();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private record GmailTokenResponse(@JsonProperty("access_token") String accessToken) {
    }

    private record GmailSendResponse(String id) {
    }
}
