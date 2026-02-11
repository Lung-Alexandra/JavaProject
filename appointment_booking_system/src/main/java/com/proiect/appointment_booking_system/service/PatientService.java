package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.dto.PatientDTO;
import com.proiect.appointment_booking_system.exceptions.UserAlreadyExists;
import com.proiect.appointment_booking_system.mapper.PatientMapper;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void registerPatient(PatientDTO patientDTO) {
        boolean user = userRepository.existsByEmail(patientDTO.getUser().getEmail());
        if (user) {
            throw new UserAlreadyExists();
        }

        Patient patient = PatientMapper.toEntity(patientDTO);
        patient.getUser().setPassword(encodePassword(patient.getUser().getPassword()));
        patientRepository.save(patient);
    }

    public Optional<PatientDTO> getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId).map(PatientMapper::toDTO);
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream().map(PatientMapper::toDTO).collect(Collectors.toList());
    }

    public void updatePatient(Long id, PatientDTO patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.setMedicalHistory(patientDTO.getMedicalHistory());
        existingPatient.setAddress(patientDTO.getAddress());

        userService.updateUser(id, patientDTO.getUser());
        patientRepository.save(existingPatient);
    }


    public void deletePatient(Long id) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patientRepository.deleteById(id);
    }

    private String encodePassword(String rawPassword) {
        if (passwordEncoder == null || rawPassword == null) {
            return rawPassword;
        }
        return passwordEncoder.encode(rawPassword);
    }

}
