package com.proiect.appointment_booking_system.controller.IntegrationTests;

import com.proiect.appointment_booking_system.controller.AppointmentController;
import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.service.AppointmentService;
import com.proiect.appointment_booking_system.enums.Status;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService service;

    @Test
    public void testGetAllAppointments() throws Exception {
        AppointmentDTO appointment1 = new AppointmentDTO();
        appointment1.setId(1L);
        appointment1.setAppointmentDate(LocalDate.of(2025, 1, 10));
        appointment1.setAppointmentTime(LocalTime.of(10, 0));
        appointment1.setDoctorId(1L);
        appointment1.setPatientId(1L);
        appointment1.setStatus(Status.BOOKED.name());

        AppointmentDTO appointment2 = new AppointmentDTO();
        appointment2.setId(2L);
        appointment2.setAppointmentDate(LocalDate.of(2025, 1, 10));
        appointment2.setAppointmentTime(LocalTime.of(11, 0));
        appointment2.setDoctorId(2L);
        appointment2.setPatientId(2L);
        appointment2.setStatus(Status.COMPLETED.name());

        List<AppointmentDTO> appointments = Arrays.asList(appointment1, appointment2);

        Mockito.when(service.getAllAppointments()).thenReturn(appointments);

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[0].appointmentDate", is("2025-01-10")))
                .andExpect(jsonPath("$[1].appointmentDate", is("2025-01-10")))
                .andExpect(jsonPath("$[0].appointmentTime", is("10:00:00")))
                .andExpect(jsonPath("$[1].appointmentTime", is("11:00:00")))
                .andExpect(jsonPath("$[0].status", is(Status.BOOKED.name())))  // Status verificat
                .andExpect(jsonPath("$[1].status", is(Status.COMPLETED.name())));  // Status verificat
    }

    @Test
    public void testCreateAppointment() throws Exception {
        AppointmentDTO newAppointment = new AppointmentDTO();
        newAppointment.setId(1L);
        newAppointment.setAppointmentDate(LocalDate.of(2025, 1, 10));
        newAppointment.setAppointmentTime(LocalTime.of(10, 0));
        newAppointment.setDoctorId(1L);
        newAppointment.setPatientId(1L);
        newAppointment.setStatus(Status.BOOKED.name());

        Mockito.doNothing().when(service).createAppointment(Mockito.any(AppointmentDTO.class));

        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "appointmentDate": "2025-01-10",
                            "appointmentTime": "10:00:00",
                            "doctorId": 1,
                            "patientId": 1,
                            "status": "BOOKED"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().string("Appointment created successfully"));
    }

}