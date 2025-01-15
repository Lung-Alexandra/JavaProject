package com.proiect.appointment_booking_system.controller.unit2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.appointment_booking_system.controller.ClinicController;
import com.proiect.appointment_booking_system.dto.ClinicDTO;
import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.service.ClinicService;
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
import java.util.Optional;

public class ClinicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClinicService clinicService;

    @InjectMocks
    private ClinicController clinicController;

    private MockService mockService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clinicController).build();
        mockService = new MockService();
    }

    @Test
    public void testRegisterClinic_success() throws Exception {
        ClinicDTO clinicDTO = mockService.getMockClinic();
        Mockito.doNothing().when(clinicService).registerClinic(clinicDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/clinics/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(clinicDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Clinic registered successfully"));
    }

    @Test
    public void testGetAllClinics_success() throws Exception {
        List<ClinicDTO> mockClinics = mockService.getMockClinics();
        Mockito.when(clinicService.getAllClinics()).thenReturn(mockClinics);

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(new ObjectMapper().writeValueAsString(mockClinics)));
    }

    @Test
    public void testGetClinicById_success() throws Exception {
        ClinicDTO clinicDTO = mockService.getMockClinic();
        Mockito.when(clinicService.getClinicById(1L)).thenReturn(Optional.of(clinicDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(new ObjectMapper().writeValueAsString(clinicDTO)));
    }

    @Test
    public void testGetClinicById_notFound() throws Exception {
        Mockito.when(clinicService.getClinicById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testAddDoctorToClinic_success() throws Exception {
        Mockito.doNothing().when(clinicService).addDoctorToClinic(1L, 2L);

        mockMvc.perform(MockMvcRequestBuilders.post("/clinics/1/doctors/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Doctor added to clinic successfully"));
    }


    @Test
    public void testRemoveDoctorFromClinic_success() throws Exception {
        Mockito.doNothing().when(clinicService).removeDoctorFromClinic(1L, 2L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clinics/1/doctors/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Doctor removed from clinic successfully"));
    }

    @Test
    public void testFilterClinicsByLocation_success() throws Exception {
        List<ClinicDTO> mockClinics = mockService.getMockClinics();
        Mockito.when(clinicService.filterClinicsByLocation("City Center")).thenReturn(mockClinics);

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics/filterByLocation")
                        .param("location", "City Center"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(new ObjectMapper().writeValueAsString(mockClinics)));
    }

    @Test
    public void testDeleteClinic_success() throws Exception {
        Mockito.doNothing().when(clinicService).deleteClinicById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clinics/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Clinic deleted successfully"));
    }

    @Test
    public void testGetDoctorsByClinicId_success() throws Exception {
        List<DoctorDTO> mockDoctors = mockService.getMockDoctors();
        Mockito.when(clinicService.getDoctorsByClinicId(1L)).thenReturn(mockDoctors);

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics/1/doctors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(mockDoctors)));
    }

    @Test
    public void testGetClinicByEmail_success() throws Exception {
        ClinicDTO mockClinic = mockService.getMockClinic();
        Mockito.when(clinicService.getClinicByEmail("clinic@example.com")).thenReturn(java.util.Optional.of(mockClinic));

        mockMvc.perform(MockMvcRequestBuilders.get("/clinics/email")
                        .param("email", "clinic@example.com"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(mockClinic)));
    }
}
