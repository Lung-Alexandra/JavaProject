package com.proiect.appointment_booking_system.dto;

import com.proiect.appointment_booking_system.enums.Role;
import com.proiect.appointment_booking_system.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public class UserDTO {

    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private String role; // PATIENT, DOCTOR, ADMIN
    private String phoneNumber;

    // Default constructor
    public UserDTO() {}

    // Constructor for creating DTO from Entity
    public UserDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = String.valueOf(user.getRole());
        this.phoneNumber = user.getPhoneNumber();
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
