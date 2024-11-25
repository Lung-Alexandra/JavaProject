package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.service.DoctorService;
import com.proiect.appointment_booking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    // --------------------- USER FUNCTIONALITIES ---------------------

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        userService.registerUser(userDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // --------------------- DOCTOR FUNCTIONALITIES ---------------------

    @PostMapping("/doctors")
    public ResponseEntity<String> registerDoctor(@RequestBody DoctorDTO doctorDTO) {
        doctorService.registerDoctor(doctorDTO);
        return ResponseEntity.ok("Doctor registered successfully");
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Integer id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<String> deleteDoctorById(@PathVariable Integer id) {
        doctorService.deleteDoctorById(id);
        return ResponseEntity.ok("Doctor deleted successfully");
    }

    @PostMapping("/doctors/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<String> assignClinicToDoctor(@PathVariable Integer doctorId, @PathVariable Integer clinicId) {
        doctorService.assignClinicToDoctor(doctorId, clinicId);
        return ResponseEntity.ok("Doctor assigned to clinic successfully");
    }
}
