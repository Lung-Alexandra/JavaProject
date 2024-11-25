package com.proiect.appointment_booking_system.repository;

import com.proiect.appointment_booking_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


    Optional<User> findByEmail(String email);


    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByName(String name);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findAll();


}
