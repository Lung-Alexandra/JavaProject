package com.proiect.appointment_booking_system.exceptionHandler;


import com.proiect.appointment_booking_system.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            ClinicNotFound.class,
            DoctorNotFound.class,
            PatientNotFound.class,
            AppointmentNotFound.class,
            UserNotFoundError.class,
            UserAlreadyExists.class})
    public ResponseEntity handle(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
