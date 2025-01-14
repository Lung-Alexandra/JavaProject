package com.proiect.appointment_booking_system.mapper;


import com.proiect.appointment_booking_system.dto.PatientDTO;
import com.proiect.appointment_booking_system.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public static PatientDTO toDTO(Patient patient) {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setUser(UserMapper.toDTO(patient.getUser()));
        patientDTO.setMedicalHistory(patient.getMedicalHistory());
        patientDTO.setAddress(patient.getAddress());
        return patientDTO;
    }

    public static Patient toEntity(PatientDTO patientDTO) {
        Patient patient = new Patient();
        patient.setUser(UserMapper.toEntity(patientDTO.getUser()));
        patient.setMedicalHistory(patientDTO.getMedicalHistory());
        patient.setAddress(patientDTO.getAddress());
        return patient;
    }
}
