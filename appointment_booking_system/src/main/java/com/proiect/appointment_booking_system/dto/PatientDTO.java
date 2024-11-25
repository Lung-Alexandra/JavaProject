package com.proiect.appointment_booking_system.dto;

public class PatientDTO {
    private Integer userId;
    private String medicalHistory;
    private String address;

    // Getters and Setters
    // ...

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
