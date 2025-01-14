package com.proiect.appointment_booking_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.appointment_booking_system.AppointmentBookingSystemApplication;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AppointmentBookingSystemApplication.class)
@AutoConfigureMockMvc
@Transactional
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ObjectMapper objectMapper; // For JSON serialization


    @Test
    public void testRegisterDoctor() throws Exception {
//        User user = new User();
//        user.setName("Dr. Alice");
//        user.setEmail("alice@example.com");
//        user.setPassword("password123");
//
//        Doctor doctor = new Doctor();
//        doctor.setUser(user);
//        doctor.setSpecialization("Pediatrician");
//        doctor.setAvailabilitySchedule("Monday: 08:00-16:00");
//
//        mockMvc.perform(post("/doctors/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(doctor)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.user.name").value("Dr. Alice"))
//                .andExpect(jsonPath("$.specialization").value("Pediatrician"));
    }


}