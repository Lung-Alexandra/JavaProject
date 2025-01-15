package com.proiect.appointment_booking_system.controller.unit2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.appointment_booking_system.controller.NotificationController;
import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.service.NotificationService;
import com.proiect.appointment_booking_system.controller.test_service.MockService;
import org.hamcrest.CoreMatchers;
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

public class NotificationControllerTest {

    private MockService mockService;
    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        mockService = new MockService();
    }

    @Test
    public void testGetAllNotifications_success() throws Exception {
        List<NotificationDTO> mockNotifications = mockService.getMockNotifications();
        Mockito.when(notificationService.getAllNotifications()).thenReturn(mockNotifications);

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(mockNotifications)));
    }

    @Test
    public void testCreateNotification_success() throws Exception {
        NotificationDTO mockNotification = mockService.getMockNotification();
        Mockito.when(notificationService.createNotification(mockNotification)).thenReturn(mockNotification);

        mockMvc.perform(MockMvcRequestBuilders.post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockNotification)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}