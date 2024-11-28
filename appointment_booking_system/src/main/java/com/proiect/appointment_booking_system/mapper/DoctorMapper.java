package com.proiect.appointment_booking_system.mapper;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public static DoctorDTO toDTO(Doctor doctor) {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctor.getId());
        doctorDTO.setSpecialization(doctor.getSpecialization());
        doctorDTO.setUser(UserMapper.toDTO(doctor.getUser()));
        doctorDTO.setAvailabilitySchedule(doctor.getAvailabilitySchedule());
        doctorDTO.setClinicId(doctor.getClinic().getId());
        return doctorDTO;
    }

    public static Doctor toEntity(DoctorDTO doctorDTO, Clinic clinic) {
        Doctor doctor = new Doctor();
        doctor.setUser(UserMapper.toEntity(doctorDTO.getUser()));
        doctor.setId(doctorDTO.getId());
        doctor.setAvailabilitySchedule(doctorDTO.getAvailabilitySchedule());
        doctor.setClinic(clinic);
        doctor.setSpecialization(doctorDTO.getSpecialization());
        return doctor;
    }
}
