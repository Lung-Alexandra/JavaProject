//package com.proiect.appointment_booking_system.controller;
//
//import com.proiect.appointment_booking_system.dto.ClinicDTO;
//import com.proiect.appointment_booking_system.dto.DoctorDTO;
//import com.proiect.appointment_booking_system.dto.UserDTO;
//import com.proiect.appointment_booking_system.service.ClinicService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.Optional;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ClinicController.class)
//public class ClinicControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ClinicService clinicService;
//
//    @Test
//    public void testRegisterClinic() throws Exception {
//        Mockito.doNothing().when(clinicService).registerClinic(Mockito.any(ClinicDTO.class));
//
//        mockMvc.perform(post("/clinics/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                {
//                    "name": "HealthCare Clinic",
//                    "location": "123 Main Street",
//                    "contactNumber": "123456789",
//                    "email": "contact@healthcare.com"
//                }
//            """))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Clinic registered successfully"));
//    }
//
//    @Test
//    public void testGetClinicById() throws Exception {
//        ClinicDTO clinic = new ClinicDTO();
//        clinic.setId(1L);
//        clinic.setName("HealthCare Clinic");
//        clinic.setLocation("123 Main Street");
//        clinic.setContactNumber("123456789");
//        clinic.setEmail("contact@healthcare.com");
//
//        Mockito.when(clinicService.getClinicById(1L)).thenReturn(Optional.of(clinic));
//
//        mockMvc.perform(get("/clinics/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is("HealthCare Clinic")))
//                .andExpect(jsonPath("$.location", is("123 Main Street")))
//                .andExpect(jsonPath("$.contactNumber", is("123456789")))
//                .andExpect(jsonPath("$.email", is("contact@healthcare.com")));
//    }
//
//    @Test
//    public void testGetClinicById_NotFound() throws Exception {
//        Mockito.when(clinicService.getClinicById(1L)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/clinics/1"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void testGetClinicByEmail() throws Exception {
//        ClinicDTO clinic = new ClinicDTO();
//        clinic.setName("HealthCare Clinic");
//        clinic.setEmail("contact@healthcare.com");
//        clinic.setLocation("123 Main Street");
//        clinic.setContactNumber("123456789");
//
//        Mockito.when(clinicService.getClinicByEmail("contact@healthcare.com")).thenReturn(Optional.of(clinic));
//
//        mockMvc.perform(get("/clinics/email")
//                        .param("email", "contact@healthcare.com"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is("HealthCare Clinic")))
//                .andExpect(jsonPath("$.email", is("contact@healthcare.com")));
//    }
//
//    @Test
//    public void testGetAllClinics() throws Exception {
//        ClinicDTO clinic1 = new ClinicDTO();
//        clinic1.setName("HealthCare Clinic");
//        clinic1.setLocation("123 Main Street");
//        clinic1.setContactNumber("123456789");
//
//        ClinicDTO clinic2 = new ClinicDTO();
//        clinic2.setName("Wellness Center");
//        clinic2.setLocation("456 Elm Street");
//        clinic2.setContactNumber("987654321");
//
//        Mockito.when(clinicService.getAllClinics()).thenReturn(Arrays.asList(clinic1, clinic2));
//
//        mockMvc.perform(get("/clinics"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("HealthCare Clinic")))
//                .andExpect(jsonPath("$[0].location", is("123 Main Street")))
//                .andExpect(jsonPath("$[1].name", is("Wellness Center")))
//                .andExpect(jsonPath("$[1].location", is("456 Elm Street")));
//    }
//
//    @Test
//    public void testDeleteClinicById() throws Exception {
//        Mockito.doNothing().when(clinicService).deleteClinicById(1L);
//
//        mockMvc.perform(delete("/clinics/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Clinic deleted successfully"));
//    }
//
//    @Test
//    public void testGetDoctorsByClinicId() throws Exception {
//        UserDTO user1 = new UserDTO();
//        user1.setName("Dr. Smith");
//        user1.setEmail("dr.smith@example.com");
//        user1.setPhoneNumber("123456789");
//
//        DoctorDTO doctor1 = new DoctorDTO();
//        doctor1.setUser(user1);
//        doctor1.setSpecialization("Cardiology");
//
//        UserDTO user2 = new UserDTO();
//        user2.setName("Dr. Adams");
//        user2.setEmail("dr.adams@example.com");
//        user2.setPhoneNumber("987654321");
//
//        DoctorDTO doctor2 = new DoctorDTO();
//        doctor2.setUser(user2);
//        doctor2.setSpecialization("Neurology");
//
//        Mockito.when(clinicService.getDoctorsByClinicId(1L))
//                .thenReturn(Arrays.asList(doctor1, doctor2));
//
//        mockMvc.perform(get("/clinics/1/doctors"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].user.name", is("Dr. Smith")))
//                .andExpect(jsonPath("$[1].user.name", is("Dr. Adams")));
//    }
//
//    @Test
//    public void testAddDoctorToClinic() throws Exception {
//        Mockito.doNothing().when(clinicService).addDoctorToClinic(1L, 1L);
//
//        mockMvc.perform(post("/clinics/1/doctors/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Doctor added to clinic successfully"));
//    }
//
//    @Test
//    public void testRemoveDoctorFromClinic() throws Exception {
//        Mockito.doNothing().when(clinicService).removeDoctorFromClinic(1L, 1L);
//
//        mockMvc.perform(delete("/clinics/1/doctors/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Doctor removed from clinic successfully"));
//    }
//}
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
