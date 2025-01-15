package com.proiect.appointment_booking_system.controller.unit2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.appointment_booking_system.controller.DoctorController;
import com.proiect.appointment_booking_system.controller.test_service.MockService;
import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.service.DoctorService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DoctorControllerTest {
    private MockService mockService;
    private MockMvc mockMvc;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();
        mockService = new MockService();
    }

    @Test
    public void testRegisterDoctor_success() throws Exception {
        DoctorDTO doctorDTO = mockService.getMockDoctor();
        Mockito.doNothing().when(doctorService).registerDoctor(doctorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(doctorDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Doctor registered successfully"));
    }
    @Test
    public void testGetDoctorById_success() throws Exception {
        DoctorDTO doctorDTO = mockService.getMockDoctor();
        Mockito.when(doctorService.getDoctorByUserId(1L)).thenReturn(Optional.of(doctorDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(doctorDTO)));
    }

    @Test
    public void testGetDoctorById_notFound() throws Exception {
        Mockito.when(doctorService.getDoctorByUserId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetAllDoctors_success() throws Exception {
        List<DoctorDTO> mockDoctors = Arrays.asList(new DoctorDTO(), new DoctorDTO());
        Mockito.when(doctorService.getAllDoctors()).thenReturn(mockDoctors);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(new ObjectMapper().writeValueAsString(mockDoctors)));
    }

    @Test
    public void testSearchDoctorsBySpecialization_success() throws Exception {
        List<DoctorDTO> mockDoctors = mockService.getMockDoctors();
        Mockito.when(doctorService.searchDoctorsBySpecialization("Cardiology")).thenReturn(mockDoctors);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/specialization")
                        .param("specialization", "Cardiology"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(mockDoctors)));
    }



    @Test
    public void testDeleteDoctor_success() throws Exception {
        Mockito.doNothing().when(doctorService).deleteDoctorById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Doctor deleted successfully"));
    }



}
