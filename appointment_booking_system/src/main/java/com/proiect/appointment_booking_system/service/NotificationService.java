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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private static final String DEMO_EMAIL_DOMAIN = "@demo.local";

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private NotificationMapper mapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${notifications.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notifications.email.from:${spring.mail.username:}}")
    private String senderEmail;


    public List<NotificationDTO> getAllNotifications() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }


    // Creare notificare
    public NotificationDTO createNotification(NotificationDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(AppointmentNotFound::new);
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(PatientNotFound::new);

        Notification notification = mapper.toEntity(dto, patient, appointment);
        notification.setDelivered(false);
        notification.setDeliveredAt(null);
        Notification savedNotification = repository.save(notification);
        triggerImmediateSendIfDue(savedNotification);
        return mapper.toDTO(savedNotification);
    }

    public void deleteNotificationByAppointmentId(Long appointmentId) {
        Notification notification = repository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.getAppointment().setNotification(null);
        repository.delete(notification);
    }

    @Scheduled(fixedDelayString = "${notifications.scheduler.delay-ms:60000}")
    @Transactional
    public void sendDueNotifications() {
        if (!emailEnabled) {
            return;
        }
        if (mailSender == null) {
            LOGGER.warn("Email notifications are enabled, but JavaMailSender is not configured.");
            return;
        }

        List<Notification> dueNotifications =
                repository.findBySentAtLessThanEqualAndDeliveredFalse(LocalDateTime.now(ZoneOffset.UTC));
        LOGGER.info("Found {} due notifications to send.", dueNotifications.size());
        for (Notification notification : dueNotifications) {
            sendReminderEmail(notification);
        }
    }

    private void sendReminderEmail(Notification notification) {
        String recipient = notification.getPatient().getUser().getEmail();
        if (recipient == null || recipient.isBlank()) {
            LOGGER.warn("Notification {} has no recipient email configured.", notification.getId());
            return;
        }
        if (recipient.toLowerCase(Locale.ROOT).endsWith(DEMO_EMAIL_DOMAIN)) {
            LOGGER.info("Skipping reminder email for demo recipient {}.", recipient);
            notification.setDelivered(true);
            notification.setDeliveredAt(LocalDateTime.now(ZoneOffset.UTC));
            repository.save(notification);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        if (senderEmail != null && !senderEmail.isBlank()) {
            message.setFrom(senderEmail);
        }
        message.setTo(recipient);
        message.setSubject("Appointment Reminder");
        message.setText(buildReminderBody(notification));

        try {
            LOGGER.info("Sending reminder email for notification {} to {}.", notification.getId(), recipient);
            mailSender.send(message);
            notification.setDelivered(true);
            notification.setDeliveredAt(LocalDateTime.now(ZoneOffset.UTC));
            repository.save(notification);
            LOGGER.info("Successfully sent reminder for notification {} to {}.", notification.getId(), recipient);
        } catch (MailAuthenticationException exception) {
            LOGGER.error(
                    "SMTP authentication failed for sender {}. Verify MAIL_USERNAME and a valid Gmail App Password.",
                    senderEmail,
                    exception);
        } catch (Exception exception) {
            LOGGER.warn(
                    "Failed to send reminder for notification {} to {}",
                    notification.getId(),
                    recipient,
                    exception);
        }
    }

    private void triggerImmediateSendIfDue(Notification notification) {
        if (!emailEnabled || mailSender == null) {
            return;
        }
        LocalDateTime sentAt = notification.getSentAt();
        if (sentAt != null && !sentAt.isAfter(LocalDateTime.now(ZoneOffset.UTC)) && !notification.isDelivered()) {
            sendReminderEmail(notification);
        }
    }

    private String buildReminderBody(Notification notification) {
        Appointment appointment = notification.getAppointment();
        String doctorName = appointment.getDoctor().getUser().getName();
        String clinicName = appointment.getClinic().getName();

        return String.format(
                "Hi %s,%n%nThis is your reminder for the appointment on %s at %s.%nDoctor: %s%nClinic: %s%n%nThank you.",
                notification.getPatient().getUser().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                doctorName,
                clinicName);
    }
}
