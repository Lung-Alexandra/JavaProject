package com.proiect.appointment_booking_system.controller.unit2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.appointment_booking_system.controller.AppointmentController;
import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.service.AppointmentService;
import com.proiect.appointment_booking_system.controller.test_service.MockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

public class AppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private MockService mockService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
        mockService = new MockService(); // Inițializează MockService
    }

    @Test
    public void testGetAllAppointments_success() throws Exception {
        List<AppointmentDTO> mockAppointments = mockService.getMockAppointments();
        Mockito.when(appointmentService.getAllAppointments()).thenReturn(mockAppointments);

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(new ObjectMapper().writeValueAsString(mockAppointments)));
    }

    @Test
    public void testCreateAppointment_success() throws Exception {
        AppointmentDTO appointmentDTO = mockService.getMockAppointment();
        Mockito.doNothing().when(appointmentService).createAppointment(appointmentDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(appointmentDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Appointment created successfully"));
    }

    @Test
    public void testCancelAppointment_success() throws Exception {
        Mockito.doNothing().when(appointmentService).cancelAppointment(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/appointments/1/cancel"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Appointment and associated notification cancelled successfully"));
    }

    @Test
    public void testTrackPatientAppointments_success() throws Exception {
        Map<Long, Long> mockTracking = Map.of(1L, 3L, 2L, 5L);
        Mockito.when(appointmentService.trackPatientAppointments()).thenReturn(mockTracking);

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments/patients"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(new ObjectMapper().writeValueAsString(mockTracking)));
    }

    @Test
    public void testRemoveCancelledAppointments_success() throws Exception {
        Mockito.doNothing().when(appointmentService).removeAllCancelledAppointments();

        mockMvc.perform(MockMvcRequestBuilders.delete("/appointments/remove-cancelled"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("All cancelled appointments have been removed successfully"));
    }
}
