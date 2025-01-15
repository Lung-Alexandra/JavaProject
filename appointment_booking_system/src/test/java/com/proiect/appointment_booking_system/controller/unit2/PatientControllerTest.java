package com.proiect.appointment_booking_system.controller.unit2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.appointment_booking_system.controller.PatientController;
import com.proiect.appointment_booking_system.controller.test_service.MockService;
import com.proiect.appointment_booking_system.dto.PatientDTO;
import com.proiect.appointment_booking_system.service.PatientService;
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

class PatientControllerTest {

    private MockService mockService;
    private MockMvc mockMvc;


    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        mockService = new MockService();
    }

    @Test
    public void testRegisterPatient_success() throws Exception {
        PatientDTO mockPatient = mockService.getMockPatient();
        Mockito.doNothing().when(patientService).registerPatient(mockPatient);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockPatient)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Patient registered successfully"));
    }

    @Test
    public void testUpdatePatient_success() throws Exception {
        PatientDTO mockPatient = mockService.getMockPatient();
        Mockito.doNothing().when(patientService).updatePatient(1L, mockPatient);

        mockMvc.perform(MockMvcRequestBuilders.put("/patients/updatePatient/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockPatient)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Patient updated successfully"));
    }

    @Test
    public void testGetAllPatients_success() throws Exception {
        List<PatientDTO> mockPatients = mockService.getMockPatients();
        Mockito.when(patientService.getAllPatients()).thenReturn(mockPatients);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(mockPatients)));
    }

    @Test
    public void testDeletePatient_success() throws Exception {
        Mockito.doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Patient deleted successfully"));
    }


}
