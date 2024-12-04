package com.proiect.appointment_booking_system.mapper;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DoctorMapper {

    public static DoctorDTO toDTO(Doctor doctor) {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctor.getUser().getId());
        doctorDTO.setSpecialization(doctor.getSpecialization());
        doctorDTO.setUser(UserMapper.toDTO(doctor.getUser()));
        doctorDTO.setAvailabilitySchedule(doctor.getAvailabilitySchedule());
        doctorDTO.setClinicIds(
                doctor.getClinics().stream()
                        .map(Clinic::getId)
                        .collect(Collectors.toSet())
        );
        return doctorDTO;
    }

    public static Doctor toEntity(DoctorDTO doctorDTO, Set<Clinic> clinics) {
        Doctor doctor = new Doctor();
        doctor.setUser(UserMapper.toEntity(doctorDTO.getUser()));
        doctor.setAvailabilitySchedule(doctorDTO.getAvailabilitySchedule());
        doctor.setClinics(clinics);
        doctor.setSpecialization(doctorDTO.getSpecialization());
        return doctor;
    }
}
