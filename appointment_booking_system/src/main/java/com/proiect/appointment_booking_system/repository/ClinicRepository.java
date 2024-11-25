package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    Optional<Clinic> findById(Integer id);

    Optional<Clinic> findByEmail(String email);
}
