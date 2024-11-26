package com.proiect.appointment_booking_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.PERSIST)  // Cascade pentru a salva automat User
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob  // Tells Hibernate to store this as a large object (TEXT in DB)
    private String medicalHistory; // JSON sau text lung

    @Column(nullable = false)
    private String address;


    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
