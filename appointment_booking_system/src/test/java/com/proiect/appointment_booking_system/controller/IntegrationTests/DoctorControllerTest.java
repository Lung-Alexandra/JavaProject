//package com.proiect.appointment_booking_system.controller;
//
//import com.proiect.appointment_booking_system.dto.DoctorDTO;
//import com.proiect.appointment_booking_system.dto.UserDTO;
//import com.proiect.appointment_booking_system.service.DoctorService;
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
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.hamcrest.Matchers.*;
//
//@WebMvcTest(DoctorController.class)
//public class DoctorControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private DoctorService doctorService;
//
//    @Test
//    public void testRegisterDoctor() throws Exception {
//        Mockito.doNothing().when(doctorService).registerDoctor(Mockito.any(DoctorDTO.class));
//
//        mockMvc.perform(post("/doctors/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                    {
//                        "user": {
//                            "name": "Dr. John Doe",
//                            "email": "john.doe@hospital.com",
//                            "phoneNumber": "1234567890",
//                            "role": "DOCTOR"
//                        },
//                        "specialization": "Cardiology",
//                        "experience": 10
//                    }
//                """))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Doctor registered successfully"));
//    }
//
//    @Test
//    public void testGetDoctorById() throws Exception {
//        DoctorDTO doctor = new DoctorDTO();
//        UserDTO user = new UserDTO();
//        user.setName("Dr. John Doe");
//        user.setEmail("john.doe@hospital.com");
//        user.setPhoneNumber("1234567890");
//        user.setRole("DOCTOR");
//        doctor.setUser(user);
//        doctor.setSpecialization("Cardiology");
//
//        Mockito.when(doctorService.getDoctorByUserId(1L)).thenReturn(Optional.of(doctor));
//
//        mockMvc.perform(get("/doctors/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.user.name", is("Dr. John Doe")))
//                .andExpect(jsonPath("$.user.email", is("john.doe@hospital.com")))
//                .andExpect(jsonPath("$.user.phoneNumber", is("1234567890")))
//                .andExpect(jsonPath("$.user.role", is("DOCTOR")))
//                .andExpect(jsonPath("$.specialization", is("Cardiology")));
//    }
//
//    @Test
//    public void testGetDoctorById_NotFound() throws Exception {
//        Mockito.when(doctorService.getDoctorByUserId(1L)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/doctors/1"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void testGetAllDoctors() throws Exception {
//        DoctorDTO doctor1 = new DoctorDTO();
//        UserDTO user1 = new UserDTO();
//        user1.setName("Dr. John Doe");
//        user1.setEmail("john.doe@hospital.com");
//        user1.setPhoneNumber("1234567890");
//        user1.setRole("DOCTOR");
//        doctor1.setUser(user1);
//        doctor1.setSpecialization("Cardiology");
//
//        DoctorDTO doctor2 = new DoctorDTO();
//        UserDTO user2 = new UserDTO();
//        user2.setName("Dr. Jane Smith");
//        user2.setEmail("jane.smith@hospital.com");
//        user2.setPhoneNumber("0987654321");
//        user2.setRole("DOCTOR");
//        doctor2.setUser(user2);
//        doctor2.setSpecialization("Neurology");
//
//        Mockito.when(doctorService.getAllDoctors()).thenReturn(Arrays.asList(doctor1, doctor2));
//
//        mockMvc.perform(get("/doctors"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].user.name", is("Dr. John Doe")))
//                .andExpect(jsonPath("$[0].user.email", is("john.doe@hospital.com")))
//                .andExpect(jsonPath("$[0].user.phoneNumber", is("1234567890")))
//                .andExpect(jsonPath("$[0].user.role", is("DOCTOR")))
//                .andExpect(jsonPath("$[0].specialization", is("Cardiology")))
//                .andExpect(jsonPath("$[1].user.name", is("Dr. Jane Smith")))
//                .andExpect(jsonPath("$[1].user.email", is("jane.smith@hospital.com")))
//                .andExpect(jsonPath("$[1].user.phoneNumber", is("0987654321")))
//                .andExpect(jsonPath("$[1].user.role", is("DOCTOR")))
//                .andExpect(jsonPath("$[1].specialization", is("Neurology")));
//    }
//
//    @Test
//    public void testDeleteDoctorById() throws Exception {
//        Mockito.doNothing().when(doctorService).deleteDoctorById(1L);
//
//        mockMvc.perform(delete("/doctors/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Doctor deleted successfully"));
//    }
//
//}
package com.proiect.appointment_booking_system.controller.IntegrationTests;

import com.proiect.appointment_booking_system.controller.DoctorController;
import com.proiect.appointment_booking_system.dto.DoctorDTO;
import com.proiect.appointment_booking_system.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        doNothing().when(doctorService).registerDoctor(doctorDTO);

        String response = String.valueOf(doctorController.registerDoctor(doctorDTO));

        assertEquals("<200 OK OK,Doctor registered successfully,[]>", response);
        verify(doctorService, times(1)).registerDoctor(doctorDTO);
    }

    @Test
    void testGetAllDoctors() {
        DoctorDTO doctor1 = new DoctorDTO();
        doctor1.setSpecialization("Cardiology");
        DoctorDTO doctor2 = new DoctorDTO();
        doctor2.setSpecialization("Dermatology");

        when(doctorService.getAllDoctors()).thenReturn(Arrays.asList(doctor1, doctor2));

        List<DoctorDTO> result = doctorController.getAllDoctors().getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cardiology", result.get(0).getSpecialization());
        verify(doctorService, times(1)).getAllDoctors();
    }

    @Test
    void testDeleteDoctorById() {
        doNothing().when(doctorService).deleteDoctorById(1L);

        String response = String.valueOf(doctorController.deleteDoctorById(1L));

        assertEquals("<200 OK OK,Doctor deleted successfully,[]>", response);
        verify(doctorService, times(1)).deleteDoctorById(1L);
    }

    @Test
    void testSearchDoctorsBySpecialization() {
        DoctorDTO doctor1 = new DoctorDTO();
        doctor1.setSpecialization("Cardiology");

        when(doctorService.searchDoctorsBySpecialization("Cardiology")).thenReturn(Collections.singletonList(doctor1));

        List<DoctorDTO> result = doctorController.searchDoctorsBySpecialization("Cardiology").getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cardiology", result.get(0).getSpecialization());
        verify(doctorService, times(1)).searchDoctorsBySpecialization("Cardiology");
    }
}
