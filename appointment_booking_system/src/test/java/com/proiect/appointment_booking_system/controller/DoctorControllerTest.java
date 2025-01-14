package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorControllerTest {

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterDoctor() {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(1L);
        doctorDTO.setSpecialization("Cardiology");

        doNothing().when(doctorService).registerDoctor(doctorDTO);

        var response = doctorController.registerDoctor(doctorDTO);

        assertEquals("<200 OK OK,Doctor registered successfully,[]>", response.toString());
        verify(doctorService, times(1)).registerDoctor(doctorDTO);
    }

    @Test
    void testGetDoctorById_Found() {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(1L);
        doctorDTO.setSpecialization("Cardiology");

        when(doctorService.getDoctorByUserId(1L)).thenReturn(Optional.of(doctorDTO));

        var result = doctorController.getDoctorById(1L);

        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        verify(doctorService, times(1)).getDoctorByUserId(1L);
    }

    @Test
    void testGetDoctorById_NotFound() {
        when(doctorService.getDoctorByUserId(1L)).thenReturn(Optional.empty());

        var result = doctorController.getDoctorById(1L);

        assertNull(result.getBody());
        verify(doctorService, times(1)).getDoctorByUserId(1L);
    }

    @Test
    void testGetAllDoctors() {
        DoctorDTO doctor1 = new DoctorDTO();
        doctor1.setId(1L);
        doctor1.setSpecialization("Cardiology");

        DoctorDTO doctor2 = new DoctorDTO();
        doctor2.setId(2L);
        doctor2.setSpecialization("Dermatology");

        when(doctorService.getAllDoctors()).thenReturn(Arrays.asList(doctor1, doctor2));

        var result = doctorController.getAllDoctors();

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        verify(doctorService, times(1)).getAllDoctors();
    }

    @Test
    void testDeleteDoctorById() {
        doNothing().when(doctorService).deleteDoctorById(1L);

        var response = doctorController.deleteDoctorById(1L);

        assertEquals("<200 OK OK,Doctor deleted successfully,[]>", response.toString());
        verify(doctorService, times(1)).deleteDoctorById(1L);
    }

    @Test
    void testSearchDoctorsBySpecialization() {
        DoctorDTO doctor1 = new DoctorDTO();
        doctor1.setId(1L);
        doctor1.setSpecialization("Cardiology");

        when(doctorService.searchDoctorsBySpecialization("Cardiology")).thenReturn(List.of(doctor1));

        var result = doctorController.searchDoctorsBySpecialization("Cardiology");

        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Cardiology", result.getBody().get(0).getSpecialization());
        verify(doctorService, times(1)).searchDoctorsBySpecialization("Cardiology");
    }
}
