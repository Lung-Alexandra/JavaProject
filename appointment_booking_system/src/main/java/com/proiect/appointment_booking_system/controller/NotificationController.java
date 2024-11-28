package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationDTO> getAllNotifications() {
        return service.getAllNotifications();
    }

    @PostMapping
    public NotificationDTO createNotification(@RequestBody NotificationDTO dto) {
        return service.createNotification(dto);
    }
}
