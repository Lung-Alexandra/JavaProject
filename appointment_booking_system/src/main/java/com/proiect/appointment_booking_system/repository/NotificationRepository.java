package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
