package com.proiect.appointment_booking_system.controller;


import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.dto.PatientDTO;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<String> registerPatient(@RequestBody @Valid PatientDTO patient) {
         patientService.registerPatient(patient);
        return ResponseEntity.ok("Patient registered successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable Long id) {
        return patientService.getPatientByUserId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PutMapping("/updatePatient/{id}")
    public ResponseEntity<String> updatePatient(@PathVariable Long id,@RequestBody @Valid PatientDTO patient) {
        patientService.updatePatient(id,patient);
        return ResponseEntity.ok("Patient updated successfully");
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok("Patient deleted successfully");
    }
}
