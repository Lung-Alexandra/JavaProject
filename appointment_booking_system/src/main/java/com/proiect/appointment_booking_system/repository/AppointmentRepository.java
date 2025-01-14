package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Long countByPatientId(@Param("patientId") Long patientId);
}
