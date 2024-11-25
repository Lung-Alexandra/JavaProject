package com.proiect.appointment_booking_system.exceptions;

public class ClinicNotFound extends RuntimeException{
    public ClinicNotFound() {
        super("Clinic not found!");
    }
}
