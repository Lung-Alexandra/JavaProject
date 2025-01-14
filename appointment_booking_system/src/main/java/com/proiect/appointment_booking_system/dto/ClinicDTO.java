package com.proiect.appointment_booking_system.dto;

import java.util.List;

import jakarta.validation.constraints.*;

public class ClinicDTO {
    private Long id;

    @NotNull(message = "Clinic name cannot be null")
    @Size(min = 2, max = 100, message = "Clinic name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Location cannot be null")
    private String location;

    @NotNull(message = "Contact number cannot be null")
    @Pattern(regexp = "\\d{10}", message = "Contact number must be 10 digits")
    private String contactNumber;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
    private List<DoctorDTO> doctors;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<DoctorDTO> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<DoctorDTO> doctors) {
        this.doctors = doctors;
    }
}

