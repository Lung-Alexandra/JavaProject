package com.proiect.appointment_booking_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "Doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.PERSIST)  // Cascade pentru a salva automat User
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String specialization;


    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = false)
    @JsonIgnore // Ignoră câmpul clinic pentru a evita recursivitatea
    private Clinic clinic;

    @Column(nullable = false)
    private String availabilitySchedule;

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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }


    public String getAvailabilitySchedule() {
        return availabilitySchedule;
    }

    public void setAvailabilitySchedule(String availabilitySchedule) {
        this.availabilitySchedule = availabilitySchedule;
    }
}
