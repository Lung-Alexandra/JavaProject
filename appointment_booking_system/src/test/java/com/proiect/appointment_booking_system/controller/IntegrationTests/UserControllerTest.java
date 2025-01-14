package com.proiect.appointment_booking_system.controller.IntegrationTests;

import com.proiect.appointment_booking_system.controller.UserController;
import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.service.UserService;
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

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterUser() throws Exception {
        Mockito.doNothing().when(userService).registerUser(Mockito.any(UserDTO.class));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "John Doe",
                        "email": "john.doe@example.com",
                        "password": "password123",
                        "role": "PATIENT",
                        "phoneNumber": "1234567890"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testGetUserById() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("PATIENT");
        userDTO.setPhoneNumber("1234567890");

        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.password", is("password123")))
                .andExpect(jsonPath("$.role", is("PATIENT")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UserDTO user1 = new UserDTO();
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password123");
        user1.setRole("PATIENT");
        user1.setPhoneNumber("1234567890");

        UserDTO user2 = new UserDTO();
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword("password456");
        user2.setRole("DOCTOR");
        user2.setPhoneNumber("0987654321");

        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[0].password", is("password123")))
                .andExpect(jsonPath("$[0].role", is("PATIENT")))
                .andExpect(jsonPath("$[0].phoneNumber", is("1234567890")))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].email", is("jane.smith@example.com")))
                .andExpect(jsonPath("$[1].password", is("password456")))
                .andExpect(jsonPath("$[1].role", is("DOCTOR")))
                .andExpect(jsonPath("$[1].phoneNumber", is("0987654321")));
    }

    @Test
    public void testDeleteUserById() throws Exception {
        Mockito.doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }
}
