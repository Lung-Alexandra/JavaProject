
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
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAppointments() {
        AppointmentDTO appointment1 = new AppointmentDTO();
        appointment1.setId(1L);
        AppointmentDTO appointment2 = new AppointmentDTO();
        appointment2.setId(2L);

        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(appointment1, appointment2));

        List<AppointmentDTO> result = appointmentController.getAllAppointments();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentService, times(1)).getAllAppointments();
    }

    @Test
    void testCreateAppointment() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        doNothing().when(appointmentService).createAppointment(appointmentDTO);

        String response = String.valueOf(appointmentController.createAppointment(appointmentDTO));

        assertEquals("<200 OK OK,Appointment created successfully,[]>", response);
        verify(appointmentService, times(1)).createAppointment(appointmentDTO);
    }

    @Test
    void testCancelAppointment() {
        doNothing().when(appointmentService).cancelAppointment(1L);

        String response = String.valueOf(appointmentController.cancelAppointment(1L));

        assertEquals("<200 OK OK,Appointment and associated notification cancelled successfully,[]>", response);
        verify(appointmentService, times(1)).cancelAppointment(1L);
    }

    @Test
    void testTrackPatientAppointments() {
        Map<Long, Long> patientAppointments = new HashMap<>();
        patientAppointments.put(1L, 5L);
        patientAppointments.put(2L, 3L);

        when(appointmentService.trackPatientAppointments()).thenReturn(patientAppointments);

        Map<Long, Long> result = appointmentController.trackPatientAppointments().getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5L, result.get(1L));
        verify(appointmentService, times(1)).trackPatientAppointments();
    }
}