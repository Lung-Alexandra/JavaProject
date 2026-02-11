package com.proiect.appointment_booking_system.controller.test_service;

import com.proiect.appointment_booking_system.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MockService {

    public AppointmentDTO getMockAppointment() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(1L);
        dto.setPatientId(101L);
        dto.setDoctorId(201L);
        dto.setClinicId(301L);
        dto.setDurationMinutes(30);
        dto.setStatus("BOOKED");
        return dto;
    }

    public ClinicDTO getMockClinic() {
        ClinicDTO dto = new ClinicDTO();
        dto.setId(1L);
        dto.setName("Health Clinic");
        dto.setLocation("City Center");
        return dto;
    }

    public UserDTO getMockUserP() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password");
        userDTO.setRole("PATIENT");
        userDTO.setPhoneNumber("1234567890");
        return userDTO;
    }

    public PatientDTO getMockPatient() {
        PatientDTO patientDTO = new PatientDTO();
        UserDTO userDTO = getMockUserP();
        patientDTO.setUser(userDTO);
        patientDTO.setMedicalHistory("No allergies");
        patientDTO.setAddress("123 Main St, City");
        return patientDTO;
    }

    private UserDTO getMockUserD() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Alex Doe");
        userDTO.setEmail("alex.doe@example.com");
        userDTO.setPassword("password");
        userDTO.setRole("DOCTOR");
        userDTO.setPhoneNumber("1234567890");
        return userDTO;
    }

    public DoctorDTO getMockDoctor() {
        DoctorDTO doctorDTO = new DoctorDTO();
        UserDTO userDTO = getMockUserD();
        doctorDTO.setUser(userDTO);
        doctorDTO.setSpecialization("Cardiology");
        doctorDTO.setAvailabilitySchedule("5-18");
        return doctorDTO;
    }

    public List<AppointmentDTO> getMockAppointments() {
        return List.of(getMockAppointment());
    }

    public List<DoctorDTO> getMockDoctors() {
        return List.of(getMockDoctor());
    }

    public List<ClinicDTO> getMockClinics() {
        return List.of(getMockClinic());
    }
    public List<PatientDTO> getMockPatients() {
        return List.of(getMockPatient());
    }

    public List<UserDTO> getMockUsers() {
        return List.of(getMockUserP(), getMockUserD());
    }
    public  NotificationDTO getMockNotification() {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(1L);
        notificationDTO.setAppointmentId(1L);
        notificationDTO.setPatientId(101L);

//        LocalDateTime notificationTime = LocalDateTime.now().minusDays(1);
        notificationDTO.setNotificationType("REMINDER");
//        notificationDTO.setSentAt(notificationTime);

        return notificationDTO;
    }

    public List<NotificationDTO> getMockNotifications() {
        return List.of(getMockNotification());
    }
}
