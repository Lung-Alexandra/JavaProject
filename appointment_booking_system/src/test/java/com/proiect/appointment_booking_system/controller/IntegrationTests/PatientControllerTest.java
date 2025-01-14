package com.proiect.appointment_booking_system.controller.IntegrationTests;

import com.proiect.appointment_booking_system.controller.PatientController;
import com.proiect.appointment_booking_system.dto.PatientDTO;
import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.service.PatientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    public void testRegisterPatient() throws Exception {
        Mockito.doNothing().when(patientService).registerPatient(Mockito.any(PatientDTO.class));

        mockMvc.perform(post("/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "user": {
                                            "name": "John Doe",
                                            "email": "john.doe@example.com",
                                            "password": "password123",
                                            "role": "PATIENT",
                                            "phoneNumber": "1234567890"
                                        },
                                        "medicalHistory": "No known allergies",
                                        "address": "123 Main St"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient registered successfully"));
    }

    @Test
    public void testGetPatient() throws Exception {
        UserDTO user = new UserDTO();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole("PATIENT");
        user.setPhoneNumber("1234567890");

        PatientDTO patient = new PatientDTO();
        patient.setUser(user);
        patient.setMedicalHistory("No known allergies");
        patient.setAddress("123 Main St");

        Mockito.when(patientService.getPatientByUserId(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name", is("John Doe")))
                .andExpect(jsonPath("$.user.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.user.password", is("password123")))
                .andExpect(jsonPath("$.user.role", is("PATIENT")))
                .andExpect(jsonPath("$.user.phoneNumber", is("1234567890")))
                .andExpect(jsonPath("$.medicalHistory", is("No known allergies")))
                .andExpect(jsonPath("$.address", is("123 Main St")));
    }

    @Test
    public void testGetPatient_NotFound() throws Exception {
        Mockito.when(patientService.getPatientByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllPatients() throws Exception {
        UserDTO user1 = new UserDTO();
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password123");
        user1.setRole("PATIENT");
        user1.setPhoneNumber("1234567890");

        PatientDTO patient1 = new PatientDTO();
        patient1.setUser(user1);
        patient1.setMedicalHistory("No known allergies");
        patient1.setAddress("123 Main St");

        UserDTO user2 = new UserDTO();
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword("password456");
        user2.setRole("PATIENT");
        user2.setPhoneNumber("0987654321");

        PatientDTO patient2 = new PatientDTO();
        patient2.setUser(user2);
        patient2.setMedicalHistory("Diabetes");
        patient2.setAddress("456 Elm St");

        Mockito.when(patientService.getAllPatients()).thenReturn(Arrays.asList(patient1, patient2));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].user.name", is("John Doe")))
                .andExpect(jsonPath("$[0].user.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[0].medicalHistory", is("No known allergies")))
                .andExpect(jsonPath("$[0].address", is("123 Main St")))
                .andExpect(jsonPath("$[1].user.name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].user.email", is("jane.smith@example.com")))
                .andExpect(jsonPath("$[1].medicalHistory", is("Diabetes")))
                .andExpect(jsonPath("$[1].address", is("456 Elm St")));
    }

    @Test
    public void testUpdatePatient() throws Exception {
        Mockito.doNothing().when(patientService).updatePatient(Mockito.eq(1L), Mockito.any(PatientDTO.class));

        mockMvc.perform(put("/patients/updatePatient/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "user": {
                                            "name": "John Doe",
                                            "email": "john.doe@example.com",
                                            "password": "password123",
                                            "role": "PATIENT",
                                            "phoneNumber": "1234567890"
                                        },
                                        "medicalHistory": "Updated medical history",
                                        "address": "321 Maple St"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient updated successfully"));

        Mockito.verify(patientService, Mockito.times(1))
                .updatePatient(Mockito.eq(1L), Mockito.argThat(patient ->
                        patient.getAddress().equals("321 Maple St") &&
                                patient.getMedicalHistory().equals("Updated medical history")
                ));

    }

    @Test
    public void testDeletePatient() throws Exception {
        Mockito.doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient deleted successfully"));
    }

}
