package com.proiect.appointment_booking_system.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class DoctorDTO {
    private Long id;

    @NotNull(message = "User cannot be null")
    private UserDTO user;

    @NotNull(message = "Specialization cannot be null")
    @Size(min = 2, max = 50, message = "Specialization must be between 2 and 50 characters")
    private String specialization;

    @NotNull(message = "Clinic IDs cannot be null")
    private Set<Long> clinicIds;

    @NotNull(message = "Availability schedule cannot be null")
    @Size(min = 2, message = "Availability schedule must be between 2 and 100 characters")
    private String availabilitySchedule;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }


    public Set<Long> getClinicIds() {
        return clinicIds;
    }

    public void setClinicIds(Set<Long> clinicIds) {
        this.clinicIds = clinicIds;
    }


    public String getAvailabilitySchedule() {
        return availabilitySchedule;
    }

    public void setAvailabilitySchedule(String availabilitySchedule) {
        this.availabilitySchedule = availabilitySchedule;
    }
}