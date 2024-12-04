package com.proiect.appointment_booking_system.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Patients")
public class Patient {
    @Id
    //    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST)  // Cascade pentru a salva automat User
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob  // Tells Hibernate to store this as a large object (TEXT in DB)
    private String medicalHistory; // JSON sau text lung

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;


    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

}
