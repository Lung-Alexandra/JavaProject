package com.proiect.appointment_booking_system.exceptions;

public class PatientNotFound extends RuntimeException{
    public PatientNotFound() {
        super("Patient not found!");
    }
}
