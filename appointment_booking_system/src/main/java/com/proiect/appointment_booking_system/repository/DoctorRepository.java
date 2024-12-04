package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecialization(String specialization);

    Optional<Doctor> findByUserName(String userName);

    Optional<Doctor> findByUserId(Long userId);

//    Optional<Doctor> findById(Long id);

    List<Doctor> findAll();

}
