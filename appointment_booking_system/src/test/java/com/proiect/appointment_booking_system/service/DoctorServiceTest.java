//package com.proiect.appointment_booking_system.service;
//
//import com.proiect.appointment_booking_system.dto.DoctorDTO;
//import com.proiect.appointment_booking_system.dto.UserDTO;
//import com.proiect.appointment_booking_system.exceptions.ClinicNotFound;
//import com.proiect.appointment_booking_system.exceptions.UserAlreadyExists;
//import com.proiect.appointment_booking_system.model.Clinic;
//import com.proiect.appointment_booking_system.model.Doctor;
//import com.proiect.appointment_booking_system.model.User;
//import com.proiect.appointment_booking_system.repository.ClinicRepository;
//import com.proiect.appointment_booking_system.repository.DoctorRepository;
//import com.proiect.appointment_booking_system.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class DoctorServiceTest {
//
//    @Mock
//    private DoctorRepository doctorRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ClinicRepository clinicRepository;
//
//    @InjectMocks
//    private DoctorService doctorService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testRegisterDoctor_Success() {
//        // Create and set up the DoctorDTO
//        DoctorDTO doctorDTO = new DoctorDTO();
//        UserDTO userDTO = new UserDTO();
//        userDTO.setName("Test Name");
//        userDTO.setEmail("test@example.com");
//        doctorDTO.setUser(userDTO);
//        doctorDTO.setClinicIds(Set.of(1L, 2L));
//        doctorDTO.setSpecialization("Cardiology");
//
//        // Mock repository behavior
//        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
//        when(clinicRepository.findAllById(doctorDTO.getClinicIds()))
//                .thenReturn(List.of(new Clinic(1L), new Clinic(2L)));
//
//        // Execute the service method
//        doctorService.registerDoctor(doctorDTO);
//
//        // Verify that the repository's save method was called
//        verify(doctorRepository, times(1)).save(any(Doctor.class));
//    }
//
//
//    @Test
//    void testRegisterDoctor_UserAlreadyExists() {
//        DoctorDTO doctorDTO = new DoctorDTO();
//        doctorDTO.setUser(new UserDTO("test@example.com", "Test Name"));
//
//        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
//
//        assertThrows(UserAlreadyExists.class, () -> doctorService.registerDoctor(doctorDTO));
//        verify(doctorRepository, never()).save(any(Doctor.class));
//    }
//
//    @Test
//    void testRegisterDoctor_ClinicNotFound() {
//        DoctorDTO doctorDTO = new DoctorDTO();
//        doctorDTO.setUser(new UserDTO("test@example.com", "Test Name"));
//        doctorDTO.setClinicIds(Set.of(1L, 2L));
//
//        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
//        when(clinicRepository.findAllById(doctorDTO.getClinicIds())).thenReturn(Collections.singletonList(new Clinic(1L)));
//
//        assertThrows(ClinicNotFound.class, () -> doctorService.registerDoctor(doctorDTO));
//        verify(doctorRepository, never()).save(any(Doctor.class));
//    }
//
//    @Test
//    void testGetDoctorByUserId_Success() {
//        Long userId = 1L;
//        Doctor doctor = new Doctor();
//        doctor.setUser(new User(userId,"Name",));
//        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
//
//        Optional<DoctorDTO> result = doctorService.getDoctorByUserId(userId);
//
//        assertTrue(result.isPresent());
//        assertEquals(userId, result.get().getUser().getId());
//    }
//
//    @Test
//    void testGetAllDoctors() {
//        Doctor doctor = new Doctor();
//        doctor.setSpecialization("Cardiology");
//        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
//
//        var result = doctorService.getAllDoctors();
//
//        assertFalse(result.isEmpty());
//        assertEquals("Cardiology", result.get(0).getSpecialization());
//    }
//
//    @Test
//    void testDeleteDoctorById() {
//        Long doctorId = 1L;
//
//        doctorService.deleteDoctorById(doctorId);
//
//        verify(doctorRepository, times(1)).deleteById(doctorId);
//    }
//
//    @Test
//    void testSearchDoctorsBySpecialization() {
//        String specialization = "Cardiology";
//        Doctor doctor = new Doctor();
//        doctor.setSpecialization(specialization);
//        when(doctorRepository.findBySpecialization(specialization)).thenReturn(List.of(doctor));
//
//        var result = doctorService.searchDoctorsBySpecialization(specialization);
//
//        assertFalse(result.isEmpty());
//        assertEquals(specialization, result.get(0).getSpecialization());
//    }
//}


package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.enums.Role;
import com.proiect.appointment_booking_system.exceptions.ClinicNotFound;
import com.proiect.appointment_booking_system.exceptions.UserAlreadyExists;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterDoctor_Success() {
        DoctorDTO doctorDTO = createDoctorDTO();

        // Mock repository behavior
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(clinicRepository.findAllById(doctorDTO.getClinicIds()))
                .thenReturn(List.of(createClinic(1L), createClinic(2L)));

        // Execute service method
        doctorService.registerDoctor(doctorDTO);

        // Verify interactions
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void testRegisterDoctor_UserAlreadyExists() {
        DoctorDTO doctorDTO = createDoctorDTO();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExists.class, () -> doctorService.registerDoctor(doctorDTO));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testRegisterDoctor_ClinicNotFound() {
        DoctorDTO doctorDTO = createDoctorDTO();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(clinicRepository.findAllById(doctorDTO.getClinicIds()))
                .thenReturn(Collections.singletonList(createClinic(1L)));

        assertThrows(ClinicNotFound.class, () -> doctorService.registerDoctor(doctorDTO));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testGetDoctorByUserId_Success() {
        Long userId = 1L;
        Doctor doctor = createDoctor(userId);

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));

        Optional<DoctorDTO> result = doctorService.getDoctorByUserId(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUser().getId());
    }

    @Test
    void testGetAllDoctors() {
        Doctor doctor = createDoctor(1L);
        doctor.setSpecialization("Cardiology");

        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        var result = doctorService.getAllDoctors();

        assertFalse(result.isEmpty());
        assertEquals("Cardiology", result.get(0).getSpecialization());
    }

    @Test
    void testDeleteDoctorById() {
        Long doctorId = 1L;

        doctorService.deleteDoctorById(doctorId);

        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    void testSearchDoctorsBySpecialization() {
        String specialization = "Cardiology";
        Doctor doctor = createDoctor(1L);

        doctor.setSpecialization(specialization);

        when(doctorRepository.findBySpecialization(specialization)).thenReturn(List.of(doctor));

        List<DoctorDTO> result = doctorService.searchDoctorsBySpecialization(specialization);

        assertFalse(result.isEmpty());
        assertEquals(specialization, result.get(0).getSpecialization());
    }

    // Helper method to create DoctorDTO
    private DoctorDTO createDoctorDTO() {
        DoctorDTO doctorDTO = new DoctorDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test Name");
        userDTO.setEmail("test@example.com");
        userDTO.setId(1L);
        userDTO.setPhoneNumber("1234567890");
        userDTO.setRole("DOCTOR");
        doctorDTO.setUser(userDTO);
        doctorDTO.setClinicIds(Set.of(1L, 2L));
        doctorDTO.setSpecialization("Cardiology");
        return doctorDTO;
    }

    private Doctor createDoctor(Long id) {
        Doctor doctor = new Doctor();
        User user = new User();
        user.setId(id);
        user.setName("Test Name");
        user.setEmail("test@example.com");

        user.setPhoneNumber("1234567890");
        user.setRole(Role.valueOf("DOCTOR"));
        doctor.setUser(user);
        doctor.setClinics(Set.of(createClinic(1L), createClinic(2L)));
        return doctor;
    }
    // Helper method to create Clinic
    private Clinic createClinic(Long id) {
        Clinic clinic = new Clinic();
        clinic.setId(id);
        return clinic;
    }
}
