package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByAppointmentId(Long appointmentId);
    List<Notification> findBySentAtLessThanEqualAndDeliveredFalse(LocalDateTime now);
}
