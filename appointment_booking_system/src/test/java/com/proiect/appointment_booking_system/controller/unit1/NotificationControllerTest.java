package com.proiect.appointment_booking_system.controller.unit1;

import com.proiect.appointment_booking_system.controller.NotificationController;
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
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllNotifications() {
        NotificationDTO notification1 = new NotificationDTO();
        notification1.setNotificationType("REMINDER");
        NotificationDTO notification2 = new NotificationDTO();
        notification2.setNotificationType("CANCELLATION");

        when(notificationService.getAllNotifications()).thenReturn(Arrays.asList(notification1, notification2));

        List<NotificationDTO> result = notificationController.getAllNotifications();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("REMINDER", result.get(0).getNotificationType());
        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    void testCreateNotification() {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setNotificationType("REMINDER");

        when(notificationService.createNotification(notificationDTO)).thenReturn(notificationDTO);

        NotificationDTO result = notificationController.createNotification(notificationDTO);

        assertNotNull(result);
        assertEquals("REMINDER", result.getNotificationType());
        verify(notificationService, times(1)).createNotification(notificationDTO);
    }
}
