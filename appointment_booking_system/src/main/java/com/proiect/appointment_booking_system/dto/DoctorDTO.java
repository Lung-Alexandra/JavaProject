package com.proiect.appointment_booking_system.dto;


import java.util.Set;

public class DoctorDTO {
    private Long id;
    private UserDTO user;
    private String specialization;
    private Set<Long> clinicIds;
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