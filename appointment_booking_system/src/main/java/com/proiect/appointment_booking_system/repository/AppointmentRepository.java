package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.enums.Status;
import com.proiect.appointment_booking_system.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Long countByPatientId(@Param("patientId") Long patientId);

    List<Appointment> findAllByStatus(Status status);

}
