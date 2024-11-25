package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.PatientDTO;
import com.proiect.appointment_booking_system.mapper.PatientMapper;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    public void registerPatient(PatientDTO patientDTO) {
        Patient patient = patientMapper.toEntity(patientDTO);
        patientRepository.save(patient);
    }

    public Optional<PatientDTO> getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId).map(PatientMapper::toDTO);
    }
}
