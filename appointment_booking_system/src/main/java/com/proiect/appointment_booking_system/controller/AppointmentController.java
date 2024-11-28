package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return service.getAllAppointments();
    }

    @PostMapping
    public AppointmentDTO createAppointment(@RequestBody AppointmentDTO dto) {
        return service.createAppointment(dto);
    }
}
