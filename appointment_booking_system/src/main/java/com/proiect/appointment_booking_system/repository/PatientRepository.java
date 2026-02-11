package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserName(String userName);
    Optional<Patient> findByUserId(Long userId);
    Optional<Patient> findByUserEmail(String email);
}
