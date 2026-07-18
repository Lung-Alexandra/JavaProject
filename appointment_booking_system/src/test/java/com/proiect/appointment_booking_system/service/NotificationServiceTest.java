package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.mapper.NotificationMapper;
import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.Notification;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.AppointmentRepository;
import com.proiect.appointment_booking_system.repository.NotificationRepository;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationMapper notificationMapper;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "emailEnabled", true);
        ReflectionTestUtils.setField(notificationService, "senderEmail", "clinic@example.com");
    }

    @Test
    void createNotification_ShouldQueueEmailWithoutSendingItInTheRequest() {
        NotificationDTO dto = notificationDto();
        Notification notification = notification(dto.getSentAt());

        when(appointmentRepository.findById(dto.getAppointmentId()))
                .thenReturn(Optional.of(notification.getAppointment()));
        when(patientRepository.findById(dto.getPatientId()))
                .thenReturn(Optional.of(notification.getPatient()));
        when(notificationMapper.toEntity(dto, notification.getPatient(), notification.getAppointment()))
                .thenReturn(notification);
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toDTO(notification)).thenReturn(dto);

        notificationService.createNotification(dto);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendDueNotifications_ShouldSendQueuedEmailAndMarkItDelivered() {
        Notification notification = notification(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));
        when(notificationRepository.findBySentAtLessThanEqualAndDeliveredFalse(any(LocalDateTime.class)))
                .thenReturn(List.of(notification));

        notificationService.sendDueNotifications();

        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(notification);
        assertTrue(notification.isDelivered());
    }

    private NotificationDTO notificationDto() {
        NotificationDTO dto = new NotificationDTO();
        dto.setAppointmentId(100L);
        dto.setPatientId(20L);
        dto.setNotificationType("REMINDER");
        dto.setSentAt(LocalDateTime.now(ZoneOffset.UTC));
        return dto;
    }

    private Notification notification(LocalDateTime sentAt) {
        User patientUser = new User();
        patientUser.setName("Patient Test");
        patientUser.setEmail("patient@example.com");
        Patient patient = new Patient();
        patient.setId(20L);
        patient.setUser(patientUser);

        User doctorUser = new User();
        doctorUser.setName("Doctor Test");
        Doctor doctor = new Doctor();
        doctor.setId(10L);
        doctor.setUser(doctorUser);

        Clinic clinic = new Clinic();
        clinic.setId(30L);
        clinic.setName("Clinic Test");

        Appointment appointment = new Appointment();
        appointment.setId(100L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.of(10, 0));

        Notification notification = new Notification();
        notification.setId(100L);
        notification.setPatient(patient);
        notification.setAppointment(appointment);
        notification.setNotificationType("REMINDER");
        notification.setSentAt(sentAt);
        return notification;
    }
}
