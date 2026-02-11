package com.proiect.appointment_booking_system.mapper;

import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Notification;
import com.proiect.appointment_booking_system.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setPatientId(notification.getPatient().getId());
        dto.setAppointmentId(notification.getAppointment().getId());
        dto.setNotificationType(notification.getNotificationType());
        dto.setSentAt(notification.getSentAt());
        dto.setDelivered(notification.isDelivered());
        dto.setDeliveredAt(notification.getDeliveredAt());
        return dto;
    }

    public Notification toEntity(NotificationDTO dto, Patient patient, Appointment appointment) {
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setPatient(patient);
        notification.setAppointment(appointment);
        notification.setNotificationType(dto.getNotificationType());
        notification.setSentAt(dto.getSentAt());
        notification.setDelivered(dto.isDelivered());
        notification.setDeliveredAt(dto.getDeliveredAt());
        return notification;
    }
}
