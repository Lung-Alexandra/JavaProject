package com.proiect.appointment_booking_system.model;

import jakarta.persistence.*;

import java.time.*;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status; // E.g., "BOOKED", "CANCELLED", "COMPLETED"

    // Getters and setters
}
