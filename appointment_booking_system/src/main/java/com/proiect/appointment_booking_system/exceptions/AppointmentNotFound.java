package com.proiect.appointment_booking_system.exceptions;



public class AppointmentNotFound extends RuntimeException{
    public AppointmentNotFound() {
        super("Appointment not found!");
    }
}