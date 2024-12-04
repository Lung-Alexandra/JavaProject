package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.exceptions.ClinicNotFound;
import com.proiect.appointment_booking_system.exceptions.DoctorNotFound;
import com.proiect.appointment_booking_system.exceptions.PatientNotFound;
import com.proiect.appointment_booking_system.mapper.AppointmentMapper;
import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.repository.AppointmentRepository;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    private  AppointmentRepository repository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private PatientRepository patientRepository;



    public List<AppointmentDTO> getAllAppointments() {
        return repository.findAll().stream().map(AppointmentMapper::toDTO).collect(Collectors.toList());
    }

    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId()).orElseThrow(ClinicNotFound::new);
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow( PatientNotFound::new);
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow(DoctorNotFound::new);

        Appointment appointment = AppointmentMapper.toEntity(dto, patient, doctor, clinic);
        return AppointmentMapper.toDTO(repository.save(appointment));
    }
}
