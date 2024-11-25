package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
