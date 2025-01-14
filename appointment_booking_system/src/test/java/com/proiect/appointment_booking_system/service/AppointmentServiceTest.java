package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.model.*;
import com.proiect.appointment_booking_system.repository.AppointmentRepository;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAppointments() {
        // Creează și configurează entitățile asociate
        User doctorUser = new User();
        doctorUser.setId(201L);
        doctorUser.setName("Dr. John Doe");
        doctorUser.setEmail("john.doe@example.com");

        Doctor doctor = new Doctor();
        doctor.setId(201L);
        doctor.setUser(doctorUser); // Setează un User valid

        Patient patient = new Patient();
        patient.setId(101L);

        Clinic clinic = new Clinic();
        clinic.setId(301L);

        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        appointment1.setPatient(patient);
        appointment1.setDoctor(doctor); // Setează un Doctor valid
        appointment1.setClinic(clinic);

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setPatient(patient);
        appointment2.setDoctor(doctor); // Setează un Doctor valid
        appointment2.setClinic(clinic);

        // Simulează răspunsul repository-ului
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

        // Apelează metoda de testat
        var result = appointmentService.getAllAppointments();

        // Verificări
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(201L, result.get(0).getDoctorId()); // Verifică ID-ul doctorului
        assertEquals(101L, result.get(0).getPatientId()); // Verifică ID-ul pacientului

        // Verifică apelul la repository
        verify(appointmentRepository, times(1)).findAll();
    }



    @Test
    void testCreateAppointment() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientId(101L);
        appointmentDTO.setDoctorId(201L);
        appointmentDTO.setClinicId(301L);

        Patient patient = new Patient();
        patient.setId(101L);

        Doctor doctor = new Doctor();
        doctor.setId(201L);

        Clinic clinic = new Clinic();
        clinic.setId(301L);

        when(patientRepository.findById(101L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(201L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(301L)).thenReturn(Optional.of(clinic));

        doNothing().when(appointmentRepository).save(any(Appointment.class));

        appointmentService.createAppointment(appointmentDTO);

        verify(patientRepository, times(1)).findById(101L);
        verify(doctorRepository, times(1)).findById(201L);
        verify(clinicRepository, times(1)).findById(301L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
}
