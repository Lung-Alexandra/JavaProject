package com.proiect.appointment_booking_system.exceptions;

public class DoctorNotFound extends RuntimeException{
    public DoctorNotFound() {
        super("Doctor not found!");
    }
}
