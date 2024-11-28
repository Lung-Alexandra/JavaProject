package com.proiect.appointment_booking_system.dto;

import com.proiect.appointment_booking_system.enums.Status;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDTO {
    private Integer id;
    private Integer patientId;
    private Integer doctorId;
    private Integer clinicId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getClinicId() {
        return clinicId;
    }

    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
