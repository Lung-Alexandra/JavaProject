package com.proiect.appointment_booking_system.exceptions;


public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User is already existent");
    }
}

