package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.ClinicDTO;
import com.proiect.appointment_booking_system.exceptions.ClinicNotFound;
import com.proiect.appointment_booking_system.exceptions.DoctorNotFound;
import com.proiect.appointment_booking_system.mapper.ClinicMapper;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClinicService {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public void registerClinic(ClinicDTO clinicDTO) {
        Clinic clinic = ClinicMapper.toEntity(clinicDTO);
        clinicRepository.save(clinic);
    }

    public Optional<ClinicDTO> getClinicById(Long id) {
        return clinicRepository.findById(id)
                .map(ClinicMapper::toDTO);
    }

    public Optional<ClinicDTO> getClinicByEmail(String email) {
        return clinicRepository.findByEmail(email)
                .map(ClinicMapper::toDTO);
    }

    public List<ClinicDTO> getAllClinics() {
        return clinicRepository.findAll()
                .stream()
                .map(ClinicMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteClinicById(Long id) {
        clinicRepository.deleteById(id);
    }

    // Adaugă un doctor la o clinică
    public void addDoctorToClinic(Integer clinicId, Integer doctorId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(ClinicNotFound::new);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(DoctorNotFound::new);

        doctor.setClinic(clinic);
        doctorRepository.save(doctor);
    }

    // Obține lista de doctori dintr-o clinică
    public List<Doctor> getDoctorsByClinicId(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(ClinicNotFound::new);
        return clinic.getDoctors();
    }
}
