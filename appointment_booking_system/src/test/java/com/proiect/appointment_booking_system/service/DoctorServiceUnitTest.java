package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.DoctorDTO;

import com.proiect.appointment_booking_system.exceptions.*;
import com.proiect.appointment_booking_system.mapper.DoctorMapper;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.UserRepository;
import com.proiect.appointment_booking_system.controller.test_service.MockService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DoctorServiceUnitTest {

    private MockService mockService ;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private DoctorService doctorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockService = new MockService();
    }

    @Test
    public void testRegisterDoctor_success() {
        // given
        DoctorDTO mockDoctorDTO = mockService.getMockDoctor();
        Set<Long> clinicIds = Set.of(1L, 2L);
        mockDoctorDTO.setClinicIds(clinicIds);

        List<Clinic> mockClinics = clinicIds.stream()
                .map(id -> {
                    Clinic clinic = new Clinic();
                    clinic.setId(id);
                    return clinic;
                })
                .toList();

        Mockito.when(userRepository.existsByEmail(mockDoctorDTO.getUser().getEmail())).thenReturn(false);
        Mockito.when(clinicRepository.findAllById(clinicIds)).thenReturn(mockClinics);
        Mockito.when(doctorRepository.save(Mockito.any(Doctor.class)))
                .thenReturn(DoctorMapper.toEntity(mockDoctorDTO, new HashSet<>(mockClinics)));

        // when
        Assertions.assertDoesNotThrow(() -> doctorService.registerDoctor(mockDoctorDTO));
    }


    @Test
    public void testRegisterDoctor_userAlreadyExists() {
        // given
        DoctorDTO mockDoctorDTO = mockService.getMockDoctor();
        Mockito.when(userRepository.existsByEmail(mockDoctorDTO.getUser().getEmail())).thenReturn(true);

        // when & then
        Assertions.assertThrows(UserAlreadyExists.class, () -> doctorService.registerDoctor(mockDoctorDTO));
    }

    @Test
    public void testRegisterDoctor_clinicNotFound() {
        // given
        DoctorDTO mockDoctorDTO = mockService.getMockDoctor();
        Set<Long> clinicIds = Set.of(1L, 2L);
        mockDoctorDTO.setClinicIds(clinicIds);

        Mockito.when(userRepository.existsByEmail(mockDoctorDTO.getUser().getEmail())).thenReturn(false);
        Mockito.when(clinicRepository.findAllById(clinicIds)).thenReturn(List.of());

        // when & then
        Assertions.assertThrows(ClinicNotFound.class, () -> doctorService.registerDoctor(mockDoctorDTO));
    }

    @Test
    public void testGetDoctorByUserId_success() {
        // given
        Doctor mockDoctor = DoctorMapper.toEntity(mockService.getMockDoctor(), new HashSet<>());
        Mockito.when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(mockDoctor));

        // when
        Optional<DoctorDTO> result = doctorService.getDoctorByUserId(1L);

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(mockDoctor.getUser().getEmail(), result.get().getUser().getEmail());
    }

    @Test
    public void testGetDoctorByUserId_notFound() {
        // given
        Mockito.when(doctorRepository.findByUserId(99L)).thenReturn(Optional.empty());

        // when
        Optional<DoctorDTO> result = doctorService.getDoctorByUserId(99L);

        // then
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteDoctor_success() {
        // given
        Mockito.doNothing().when(doctorRepository).deleteById(1L);

        // when & then
        Assertions.assertDoesNotThrow(() -> doctorService.deleteDoctorById(1L));
        Mockito.verify(doctorRepository, Mockito.times(1)).deleteById(1L);
    }
}
