package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.ClinicDTO;
import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.mapper.DoctorMapper;
import com.proiect.appointment_booking_system.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clinics")
public class ClinicController {

    @Autowired
    private ClinicService clinicService;

    @PostMapping("/register")
    public ResponseEntity<String> registerClinic(@RequestBody ClinicDTO clinicDTO) {
        clinicService.registerClinic(clinicDTO);
        return ResponseEntity.ok("Clinic registered successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicDTO> getClinicById(@PathVariable Long id) {
        return clinicService.getClinicById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<ClinicDTO> getClinicByEmail(@RequestParam String email) {
        return clinicService.getClinicByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ClinicDTO>> getAllClinics() {
        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClinicById(@PathVariable Long id) {
        clinicService.deleteClinicById(id);
        return ResponseEntity.ok("Clinic deleted successfully");
    }


    // Endpoint pentru a obține toți doctorii dintr-o clinică
    @GetMapping("/{clinicId}/doctors")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByClinicId(@PathVariable Long clinicId) {
        List<DoctorDTO> doctorDTOS = clinicService.getDoctorsByClinicId(clinicId);
        return ResponseEntity.ok(doctorDTOS);
    }


    @PostMapping("/{clinicId}/doctors/{doctorId}")
    public ResponseEntity<String> addDoctorToClinic(@PathVariable Long clinicId, @PathVariable Long doctorId) {
        clinicService.addDoctorToClinic(clinicId, doctorId);
        return ResponseEntity.ok("Doctor added to clinic successfully");
    }

    @DeleteMapping("/{clinicId}/doctors/{doctorId}")
    public ResponseEntity<String> removeDoctorFromClinic(@PathVariable Long clinicId, @PathVariable Long doctorId) {
        clinicService.removeDoctorFromClinic(clinicId, doctorId);
        return ResponseEntity.ok("Doctor removed from clinic successfully");
    }

    @GetMapping("/filterByLocation")
    public ResponseEntity<List<ClinicDTO>> filterClinicsByLocation(@RequestParam String location) {
        return ResponseEntity.ok(clinicService.filterClinicsByLocation(location));
    }
}
