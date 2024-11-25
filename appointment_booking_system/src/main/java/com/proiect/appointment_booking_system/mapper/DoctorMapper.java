package com.proiect.appointment_booking_system.mapper;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.model.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public static DoctorDTO toDTO(Doctor doctor) {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctor.getId());
        doctorDTO.setSpecialization(doctor.getSpecialization());
        doctorDTO.setUser(UserMapper.toDTO(doctor.getUser()));
        doctorDTO.setClinic(ClinicMapper.toDTO(doctor.getClinic())); // Convertim clinica la DTO
        return doctorDTO;
    }

    public static Doctor toEntity(DoctorDTO doctorDTO) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorDTO.getId());
        doctor.setSpecialization(doctorDTO.getSpecialization());
        doctor.setUser(UserMapper.toEntity(doctorDTO.getUser()));
        doctor.setClinic(ClinicMapper.toEntity(doctorDTO.getClinic())); // Convertim DTO-ul clinicii la entitate
        return doctor;
    }
}
