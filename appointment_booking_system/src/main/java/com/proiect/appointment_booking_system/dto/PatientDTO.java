package com.proiect.appointment_booking_system.dto;

public class PatientDTO {
    private UserDTO user;
    private String medicalHistory;
    private String address;

    // Getters and Setters
    // ...

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
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
