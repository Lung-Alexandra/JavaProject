package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.enums.Status;
import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.repository.AppointmentRepository;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentReminderSchedulingTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private ClinicRepository clinicRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void createAppointment_ShouldScheduleReminderExactlyTwentyFourHoursBeforeInUtc() {
        AppointmentDTO dto = appointmentDto();
        Clinic clinic = new Clinic();
        clinic.setId(30L);
        Patient patient = new Patient();
        patient.setId(20L);
        Doctor doctor = new Doctor();
        doctor.setId(10L);
        doctor.setAvailabilitySchedule("Mon-Sun 09:00-17:00");

        when(clinicRepository.findById(dto.getClinicId())).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(dto.getPatientId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(dto.getDoctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusNot(
                eq(10L), eq(dto.getAppointmentDate()), eq(Status.CANCELLED)))
                .thenReturn(List.of());
        when(appointmentRepository.findByPatientIdAndAppointmentDateAndStatusNot(
                eq(20L), eq(dto.getAppointmentDate()), eq(Status.CANCELLED)))
                .thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(100L);
            return appointment;
        });

        appointmentService.createAppointment(dto);

        ArgumentCaptor<NotificationDTO> notificationCaptor = ArgumentCaptor.forClass(NotificationDTO.class);
        verify(notificationService).createNotification(notificationCaptor.capture());
        LocalDateTime expectedUtc = LocalDateTime.ofInstant(
                dto.getAppointmentDate()
                        .atTime(dto.getAppointmentTime())
                        .atZone(ZoneId.of("Europe/Bucharest"))
                        .toInstant()
                        .minus(Duration.ofHours(24)),
                ZoneOffset.UTC);
        assertEquals(expectedUtc, notificationCaptor.getValue().getSentAt());
    }

    private AppointmentDTO appointmentDto() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setPatientId(20L);
        dto.setDoctorId(10L);
        dto.setClinicId(30L);
        dto.setAppointmentDate(LocalDate.now(ZoneId.of("Europe/Bucharest")).plusDays(3));
        dto.setAppointmentTime(LocalTime.of(10, 0));
        dto.setDurationMinutes(30);
        dto.setStatus("BOOKED");
        return dto;
    }
}
