package com.proiect.appointment_booking_system.controller;

import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("PATIENT");
        userDTO.setPhoneNumber("1234567890");

        doNothing().when(userService).registerUser(userDTO);

        String response = String.valueOf(userController.registerUser(userDTO));

        assertEquals("<200 OK OK,User registered successfully,[]>", response);
        verify(userService, times(1)).registerUser(userDTO);
    }

    @Test
    void testGetUserById_Found() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("PATIENT");
        userDTO.setPhoneNumber("1234567890");

        when(userService.getUserById(1L)).thenReturn(Optional.of(userDTO));

        UserDTO result = userController.getUserById(1L).getBody();

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        UserDTO result = userController.getUserById(1L).getBody();

        assertNull(result);
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetAllUsers() {
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

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        var result = userController.getAllUsers().getBody();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testDeleteUserById() {
        doNothing().when(userService).deleteUserById(1L);

        String response = String.valueOf(userController.deleteUserById(1L));

        assertEquals("<200 OK OK,User deleted successfully,[]>", response);
        verify(userService, times(1)).deleteUserById(1L);
    }

}
