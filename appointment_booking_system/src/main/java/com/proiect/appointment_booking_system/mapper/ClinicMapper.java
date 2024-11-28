package com.proiect.appointment_booking_system.mapper;

import com.proiect.appointment_booking_system.dto.ClinicDTO;
import com.proiect.appointment_booking_system.model.Clinic;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ClinicMapper {

    public static ClinicDTO toDTO(Clinic clinic) {
        ClinicDTO clinicDTO = new ClinicDTO();
        clinicDTO.setId(clinic.getId());
        clinicDTO.setName(clinic.getName());
        clinicDTO.setLocation(clinic.getLocation());
        clinicDTO.setContactNumber(clinic.getContactNumber());
        clinicDTO.setEmail(clinic.getEmail());

        clinicDTO.setDoctors(clinic.getDoctors()
                .stream()
                .map(DoctorMapper::toDTO)
                .collect(Collectors.toList()));
        return clinicDTO;
    }

    public static Clinic toEntity(ClinicDTO clinicDTO) {
        Clinic clinic = new Clinic();
        clinic.setId(clinicDTO.getId());
        clinic.setName(clinicDTO.getName());
        clinic.setLocation(clinicDTO.getLocation());
        clinic.setContactNumber(clinicDTO.getContactNumber());
        clinic.setEmail(clinicDTO.getEmail());

        return clinic;
    }


}
