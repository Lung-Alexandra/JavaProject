package com.proiect.appointment_booking_system.exceptions;


public class UserNotFoundError extends RuntimeException{
    public UserNotFoundError() {
        super("User not found!");
    }
}
