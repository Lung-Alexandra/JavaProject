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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private final NotificationRepository repository;

    @Autowired
    private  PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private final NotificationMapper mapper;

    public NotificationService(NotificationRepository repository, NotificationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<NotificationDTO> getAllNotifications() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    public NotificationDTO createNotification(NotificationDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow((AppointmentNotFound::new));
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(PatientNotFound::new);

        Notification notification = mapper.toEntity(dto, patient, appointment);
        return mapper.toDTO(repository.save(notification));
    }
}
