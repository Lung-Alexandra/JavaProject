package com.proiect.appointment_booking_system.controller.unit2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.appointment_booking_system.controller.UserController;
import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.service.UserService;
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

public class UserControllerTest {

    private MockService mockService;
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        mockService = new MockService();
    }

    @Test
    public void testRegisterUser_success() throws Exception {
        UserDTO mockUser = mockService.getMockUserP();
        Mockito.doNothing().when(userService).registerUser(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully"));
    }

    @Test
    public void testGetAllUsers_success() throws Exception {
        List<UserDTO> mockUsers = mockService.getMockUsers();
        Mockito.when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(mockUsers)));
    }

    @Test
    public void testGetUserById_success() throws Exception {
        UserDTO mockUser = mockService.getMockUserP();
        Mockito.when(userService.getUserById(1L)).thenReturn(java.util.Optional.of(mockUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(mockUser)));
    }

    @Test
    public void testGetUserById_notFound() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteUser_success() throws Exception {
        Mockito.doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User deleted successfully"));
    }
}
