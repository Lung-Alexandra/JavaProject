package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.exceptions.AppointmentNotFound;
import com.proiect.appointment_booking_system.exceptions.PatientNotFound;
import com.proiect.appointment_booking_system.mapper.NotificationMapper;
import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Notification;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.repository.AppointmentRepository;
import com.proiect.appointment_booking_system.repository.NotificationRepository;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository repository;

    @Autowired
    private NotificationMapper mapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;


    public List<NotificationDTO> getAllNotifications() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }


    // Creare notificare
    public NotificationDTO createNotification(NotificationDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Notification notification = mapper.toEntity(dto, patient, appointment);

        return mapper.toDTO(repository.save(notification));
    }

    // Ștergere notificare după appointment ID
    public void deleteNotificationByAppointmentId(Long appointmentId) {
        Notification notification = repository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        repository.delete(notification);
    }
}
