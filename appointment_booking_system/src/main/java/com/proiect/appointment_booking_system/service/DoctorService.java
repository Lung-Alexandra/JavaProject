package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.exceptions.ClinicNotFound;
import com.proiect.appointment_booking_system.exceptions.UserAlreadyExists;
import com.proiect.appointment_booking_system.mapper.DoctorMapper;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClinicRepository clinicRepository;

    public void registerDoctor(DoctorDTO doctorDTO) {
        boolean user = userRepository.existsByEmail(doctorDTO.getUser().getEmail());
        if (user) {
            throw new UserAlreadyExists();
        }

        Set<Clinic> clinics = new HashSet<>(clinicRepository.findAllById(doctorDTO.getClinicIds()));

        // Verifică dacă toate clinicile există
        if (clinics.size() != doctorDTO.getClinicIds().size()) {
            throw new ClinicNotFound();
        }
        Doctor doctor = DoctorMapper.toEntity(doctorDTO,clinics);
        doctorRepository.save(doctor);
    }

    public Optional<DoctorDTO> getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId).map(DoctorMapper::toDTO);
    }

//    public Optional<DoctorDTO> getDoctorById(Long id) {
//        return doctorRepository.findById(id).map(DoctorMapper::toDTO);
//    }

    public void deleteDoctorById(Long id) {
        doctorRepository.deleteById(id);
    }


    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream().map(DoctorMapper::toDTO).collect(Collectors.toList());
    }
    
}
