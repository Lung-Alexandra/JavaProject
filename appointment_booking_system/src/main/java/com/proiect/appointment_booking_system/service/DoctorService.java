package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.exceptions.ClinicNotFound;
import com.proiect.appointment_booking_system.mapper.DoctorMapper;
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
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ClinicRepository clinicRepository;

    public void registerDoctor(DoctorDTO doctorDTO) {
        Clinic clinic = clinicRepository.findById(doctorDTO.getClinicId()).orElseThrow(ClinicNotFound::new);
        Doctor doctor = DoctorMapper.toEntity(doctorDTO,clinic);
        doctorRepository.save(doctor);
    }

    public Optional<DoctorDTO> getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId).map(DoctorMapper::toDTO);
    }

    public Optional<DoctorDTO> getDoctorById(Integer id) {
        return doctorRepository.findById(id).map(DoctorMapper::toDTO);
    }

    public void deleteDoctorById(Integer id) {
        doctorRepository.deleteById(id);
    }

    public void updateDoctor(DoctorDTO doctorDTO) {
        Clinic clinic = clinicRepository.findById(doctorDTO.getClinicId()).orElseThrow(ClinicNotFound::new);
        Doctor doctor = DoctorMapper.toEntity(doctorDTO,clinic);
        doctorRepository.save(doctor);
    }

    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream().map(DoctorMapper::toDTO).collect(Collectors.toList());
    }

    public void assignClinicToDoctor(Integer doctorId, Integer clinicId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctorRepository.save(doctor);
    }
}
