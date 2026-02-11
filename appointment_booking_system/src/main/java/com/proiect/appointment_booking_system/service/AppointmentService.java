package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.enums.Status;
import com.proiect.appointment_booking_system.exceptions.AppointmentConflictException;
import com.proiect.appointment_booking_system.exceptions.AppointmentNotFound;
import com.proiect.appointment_booking_system.exceptions.ClinicNotFound;
import com.proiect.appointment_booking_system.exceptions.DoctorNotFound;
import com.proiect.appointment_booking_system.exceptions.PatientNotFound;
import com.proiect.appointment_booking_system.mapper.AppointmentMapper;
import com.proiect.appointment_booking_system.model.*;
import com.proiect.appointment_booking_system.repository.AppointmentRepository;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 30;

    @Autowired
    private AppointmentRepository repository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NotificationService notificationService;


    public List<AppointmentDTO> getAllAppointments() {
        List<Appointment> appointments = repository.findAll();
        return appointments.stream()
                .map(AppointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForCurrentUser() {
        String email = getAuthenticatedEmail();

        List<Appointment> appointments;
        if (hasRole("PATIENT")) {
            appointments = repository.findByPatientUserEmail(email);
        } else if (hasRole("DOCTOR")) {
            appointments = repository.findByDoctorUserEmail(email);
        } else {
            appointments = repository.findAll();
        }

        return appointments.stream()
                .map(AppointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void createAppointment(AppointmentDTO dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId()).orElseThrow(ClinicNotFound::new);
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(PatientNotFound::new);
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow(DoctorNotFound::new);

        if (dto.getStatus() == null) {
            dto.setStatus(Status.BOOKED);
        }
        if (dto.getDurationMinutes() == null) {
            dto.setDurationMinutes(DEFAULT_APPOINTMENT_DURATION_MINUTES);
        }

        if (hasRole("PATIENT")) {
            String email = getAuthenticatedEmail();
            Patient authenticatedPatient = patientRepository.findByUserEmail(email).orElseThrow(PatientNotFound::new);
            if (!authenticatedPatient.getId().equals(patient.getId())) {
                throw new AccessDeniedException("Patients can only create appointments for their own account.");
            }
        }

        validateNoOverlap(doctor.getId(), patient.getId(), dto);

        Appointment appointment = AppointmentMapper.toEntity(dto, patient, doctor, clinic);
        Appointment savedAppointment = repository.save(appointment);

        LocalDateTime appointmentStart = appointment.getAppointmentDate().atTime(appointment.getAppointmentTime());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime notificationTime = appointmentStart.isAfter(now.plusDays(1))
                ? appointmentStart.minusDays(1)
                : now;
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setAppointmentId(savedAppointment.getId());
        notificationDTO.setPatientId(patient.getId());
        notificationDTO.setNotificationType("REMINDER");
        notificationDTO.setSentAt(notificationTime);

        notificationService.createNotification(notificationDTO);
    }

    /**
     * Track appointments for a specific patient
     */
    public Map<Long, Long> trackPatientAppointments() {
        return patientRepository.findAll().stream().collect(
                Collectors.toMap(
                        Patient::getId,
                        patient -> repository.countByPatientId(patient.getId())
                )
        );
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {

        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(AppointmentNotFound::new);

        if (hasRole("PATIENT")) {
            String email = getAuthenticatedEmail();
            String ownerEmail = appointment.getPatient().getUser().getEmail();
            if (!ownerEmail.equalsIgnoreCase(email)) {
                throw new AccessDeniedException("Patients can only cancel their own appointments.");
            }
        }

        try {
            notificationService.deleteNotificationByAppointmentId(appointmentId);
        } catch (RuntimeException e) {
            throw new RuntimeException("No notification found for appointment ");
        }

        appointment.setStatus(Status.CANCELLED);
        repository.save(appointment);

    }

    @Transactional
    public void removeAllCancelledAppointments() {
        List<Appointment> cancelledAppointments = repository.findAllByStatus(Status.CANCELLED);
        repository.deleteAll(cancelledAppointments);
    }

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required.");
        }
        return authentication.getName();
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        String authority = "ROLE_" + role.toUpperCase();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }

    private void validateNoOverlap(Long doctorId, Long patientId, AppointmentDTO dto) {
        LocalDateTime requestedStart = dto.getAppointmentDate().atTime(dto.getAppointmentTime());
        LocalDateTime requestedEnd = requestedStart.plusMinutes(dto.getDurationMinutes());

        List<Appointment> doctorAppointments = repository.findByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, dto.getAppointmentDate(), Status.CANCELLED);
        boolean doctorBusy = doctorAppointments.stream()
                .anyMatch(existing -> overlaps(existing, requestedStart, requestedEnd));
        if (doctorBusy) {
            throw new AppointmentConflictException("Doctor already has an overlapping appointment interval.");
        }

        List<Appointment> patientAppointments = repository.findByPatientIdAndAppointmentDateAndStatusNot(
                patientId, dto.getAppointmentDate(), Status.CANCELLED);
        boolean patientBusy = patientAppointments.stream()
                .anyMatch(existing -> overlaps(existing, requestedStart, requestedEnd));
        if (patientBusy) {
            throw new AppointmentConflictException("Patient already has an overlapping appointment interval.");
        }
    }

    private boolean overlaps(Appointment existing, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        if (existing.getAppointmentDate() == null || existing.getAppointmentTime() == null) {
            return false;
        }

        int existingDuration = existing.getDurationMinutes() == null
                ? DEFAULT_APPOINTMENT_DURATION_MINUTES
                : existing.getDurationMinutes();
        LocalDateTime existingStart = existing.getAppointmentDate().atTime(existing.getAppointmentTime());
        LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);
        return requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart);
    }

}
