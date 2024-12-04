package com.proiect.appointment_booking_system.exceptions;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists() {
        super("User is already existent");
    }
}
