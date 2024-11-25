package com.proiect.appointment_booking_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String notificationType; // E.g., "REMINDER", "CANCELLATION"
    private LocalDateTime sentAt;

    // Getters and setters
}
