package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.*;
import com.proiect.appointment_booking_system.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterPatient() {
        PatientDTO patientDTO = new PatientDTO();

        doNothing().when(patientService).registerPatient(patientDTO);

        String response = String.valueOf(patientController.registerPatient(patientDTO));

        assertEquals("<200 OK OK,Patient registered successfully,[]>", response);
        verify(patientService, times(1)).registerPatient(patientDTO);
    }

    @Test
    void testGetAllPatients() {
        PatientDTO patient1 = new PatientDTO();
        patient1.setAddress("Address 1");
        PatientDTO patient2 = new PatientDTO();
        patient2.setAddress("Address 2");

        when(patientService.getAllPatients()).thenReturn(Arrays.asList(patient1, patient2));

        List<PatientDTO> result = patientController.getAllPatients().getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Address 1", result.get(0).getAddress());
        verify(patientService, times(1)).getAllPatients();
    }

    @Test
    void testGetPatientById() {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setAddress("Address 1");

        when(patientService.getPatientByUserId(1L)).thenReturn(Optional.of(patientDTO));

        PatientDTO result = patientController.getPatient(1L).getBody();

        assertNotNull(result);
        assertEquals("Address 1", result.getAddress());
        verify(patientService, times(1)).getPatientByUserId(1L);
    }

    @Test
    void testDeletePatientById() {
        doNothing().when(patientService).deletePatient(1L);

        String response = String.valueOf(patientController.deletePatient(1L));

        assertEquals("<200 OK OK,Patient deleted successfully,[]>", response);
        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    void testUpdatePatient() {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setAddress("Updated Address");

        doNothing().when(patientService).updatePatient(1L, patientDTO);

        String response = String.valueOf(patientController.updatePatient(1L, patientDTO));

        assertEquals("<200 OK OK,Patient updated successfully,[]>", response);
        verify(patientService, times(1)).updatePatient(1L, patientDTO);
    }

    @Test
    public void testGetPatientById_NotFound() {
        when(patientService.getPatientByUserId(99L)).thenReturn(Optional.empty());

        ResponseEntity<PatientDTO> response = patientController.getPatient(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(patientService, times(1)).getPatientByUserId(99L);
    }


}
