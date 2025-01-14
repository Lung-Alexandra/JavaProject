package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return service.getAllAppointments();
    }

    @PostMapping
    public ResponseEntity<String> createAppointment(@RequestBody @Valid AppointmentDTO dto) {
        service.createAppointment(dto);
        return  ResponseEntity.ok("Appointment created successfully");
    }
    @DeleteMapping("/{appointmentId}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) {
        service.cancelAppointment(appointmentId);
        return ResponseEntity.ok("Appointment and associated notification cancelled successfully");
    }
    @GetMapping("/patients")
    public ResponseEntity<Map<Long, Long>> trackPatientAppointments() {
        return ResponseEntity.ok(service.trackPatientAppointments());
    }
    @DeleteMapping("/remove-cancelled")
    public ResponseEntity<String> removeAllCancelledAppointments() {
        service.removeAllCancelledAppointments();
        return ResponseEntity.ok("All cancelled appointments have been removed successfully");
    }

}
