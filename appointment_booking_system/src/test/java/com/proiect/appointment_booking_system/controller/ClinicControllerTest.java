package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.*;
import com.proiect.appointment_booking_system.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ClinicControllerTest {

    @Mock
    private ClinicService clinicService;

    @InjectMocks
    private ClinicController clinicController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterClinic() {
        ClinicDTO clinicDTO = new ClinicDTO();
        doNothing().when(clinicService).registerClinic(clinicDTO);

        String response = String.valueOf(clinicController.registerClinic(clinicDTO));

        assertEquals("<200 OK OK,Clinic registered successfully,[]>", response);
        verify(clinicService, times(1)).registerClinic(clinicDTO);
    }
    @Test
    void testGetClinicById_Found() {
        ClinicDTO clinicDTO = new ClinicDTO();
        clinicDTO.setId(1L);
        clinicDTO.setName("Health Clinic");

        when(clinicService.getClinicById(1L)).thenReturn(Optional.of(clinicDTO));

        var result = clinicController.getClinicById(1L);

        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        verify(clinicService, times(1)).getClinicById(1L);
    }
    @Test
    void testGetClinicById_NotFound() {
        when(clinicService.getClinicById(1L)).thenReturn(Optional.empty());

        var result = clinicController.getClinicById(1L);

        assertNull(result.getBody());
        verify(clinicService, times(1)).getClinicById(1L);
    }
    @Test
    void testGetAllClinics() {
        ClinicDTO clinic1 = new ClinicDTO();
        clinic1.setName("Clinic 1");
        ClinicDTO clinic2 = new ClinicDTO();
        clinic2.setName("Clinic 2");

        when(clinicService.getAllClinics()).thenReturn(Arrays.asList(clinic1, clinic2));

        List<ClinicDTO> result = clinicController.getAllClinics().getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Clinic 1", result.get(0).getName());
        verify(clinicService, times(1)).getAllClinics();
    }

    @Test
    void testDeleteClinicById() {
        doNothing().when(clinicService).deleteClinicById(1L);

        String response = String.valueOf(clinicController.deleteClinicById(1L));

        assertEquals("<200 OK OK,Clinic deleted successfully,[]>", response);
        verify(clinicService, times(1)).deleteClinicById(1L);
    }

    @Test
    void testAddDoctorToClinic() {
        doNothing().when(clinicService).addDoctorToClinic(1L, 1L);

        String response = String.valueOf(clinicController.addDoctorToClinic(1L, 1L));

        assertEquals("<200 OK OK,Doctor added to clinic successfully,[]>", response);
        verify(clinicService, times(1)).addDoctorToClinic(1L, 1L);
    }

    @Test
    void testRemoveDoctorFromClinic() {
        doNothing().when(clinicService).removeDoctorFromClinic(1L, 1L);

        String response = String.valueOf(clinicController.removeDoctorFromClinic(1L, 1L));

        assertEquals("<200 OK OK,Doctor removed from clinic successfully,[]>", response);
        verify(clinicService, times(1)).removeDoctorFromClinic(1L, 1L);
    }

    @Test
    void testGetDoctorsByClinicId() {
        DoctorDTO doctor1 = new DoctorDTO();
        doctor1.setSpecialization("Cardiology");
        DoctorDTO doctor2 = new DoctorDTO();
        doctor2.setSpecialization("Dermatology");

        when(clinicService.getDoctorsByClinicId(1L)).thenReturn(Arrays.asList(doctor1, doctor2));

        List<DoctorDTO> result = clinicController.getDoctorsByClinicId(1L).getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cardiology", result.get(0).getSpecialization());
        verify(clinicService, times(1)).getDoctorsByClinicId(1L);
    }

    @Test
    void testFilterClinicsByLocation() {
        ClinicDTO clinic1 = new ClinicDTO();
        clinic1.setLocation("Location 1");

        when(clinicService.filterClinicsByLocation("Location 1")).thenReturn(Collections.singletonList(clinic1));

        List<ClinicDTO> result = clinicController.filterClinicsByLocation("Location 1").getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Location 1", result.get(0).getLocation());
        verify(clinicService, times(1)).filterClinicsByLocation("Location 1");
    }
}
