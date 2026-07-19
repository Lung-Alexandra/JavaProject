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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AppointmentService {
    private static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 30;
    private static final Duration REMINDER_LEAD_TIME = Duration.ofHours(24);
    private static final Pattern DAY_SCHEDULE_PATTERN = Pattern.compile(
            "^\\s*([A-Za-z]{3,9})(?:\\s*-\\s*([A-Za-z]{3,9}))?\\s*:?\\s*(.+)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_RANGE_PATTERN = Pattern.compile(
            "(\\d{1,2})(?::(\\d{2}))?\\s*-\\s*(\\d{1,2})(?::(\\d{2}))?");
    private static final Map<String, DayOfWeek> DAY_NAMES = createDayNameMap();

    @Value("${appointments.time-zone:Europe/Bucharest}")
    private String appointmentTimeZone = "Europe/Bucharest";

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

    @Transactional
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

        validateDoctorAvailability(doctor, dto);
        validateNoOverlap(doctor.getId(), patient.getId(), dto);

        Appointment appointment = AppointmentMapper.toEntity(dto, patient, doctor, clinic);
        Appointment savedAppointment = repository.save(appointment);

        LocalDateTime notificationTime = calculateNotificationTime(appointment);
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setAppointmentId(savedAppointment.getId());
        notificationDTO.setPatientId(patient.getId());
        notificationDTO.setNotificationType("REMINDER");
        notificationDTO.setSentAt(notificationTime);

        notificationService.createNotification(notificationDTO);
    }

    private LocalDateTime calculateNotificationTime(Appointment appointment) {
        Instant now = Instant.now();
        Instant appointmentInstant = appointment.getAppointmentDate()
                .atTime(appointment.getAppointmentTime())
                .atZone(ZoneId.of(appointmentTimeZone))
                .toInstant();
        Instant reminderInstant = appointmentInstant.minus(REMINDER_LEAD_TIME);
        Instant notificationInstant = reminderInstant.isAfter(now) ? reminderInstant : now;
        return LocalDateTime.ofInstant(notificationInstant, ZoneOffset.UTC);
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

    private void validateDoctorAvailability(Doctor doctor, AppointmentDTO dto) {
        LocalTime requestedStart = dto.getAppointmentTime();
        LocalTime requestedEnd = requestedStart.plusMinutes(dto.getDurationMinutes());
        List<AvailabilityWindow> windows = availabilityWindowsForDate(
                doctor.getAvailabilitySchedule(),
                dto.getAppointmentDate().getDayOfWeek());

        boolean insideAvailability = windows.stream()
                .anyMatch(window -> !requestedStart.isBefore(window.start())
                        && !requestedEnd.isAfter(window.end()));

        if (!insideAvailability) {
            throw new AppointmentConflictException(
                    "Selected time is outside the doctor's availability schedule.");
        }
    }

    private List<AvailabilityWindow> availabilityWindowsForDate(String schedule, DayOfWeek targetDay) {
        if (schedule == null || schedule.isBlank()) {
            throw new AppointmentConflictException("Doctor availability schedule is not configured.");
        }

        List<AvailabilityWindow> windows = new ArrayList<>();
        Arrays.stream(schedule.split(";"))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .forEach(part -> addWindowsForSchedulePart(part, targetDay, windows));

        if (windows.isEmpty()) {
            throw new AppointmentConflictException(
                    "Doctor availability schedule is not configured in a supported format.");
        }
        return windows;
    }

    private void addWindowsForSchedulePart(String schedulePart, DayOfWeek targetDay, List<AvailabilityWindow> windows) {
        Matcher dayMatcher = DAY_SCHEDULE_PATTERN.matcher(schedulePart);
        if (dayMatcher.matches() && dayName(dayMatcher.group(1)) != null) {
            DayOfWeek startDay = dayName(dayMatcher.group(1));
            DayOfWeek endDay = dayName(dayMatcher.group(2));
            if (endDay == null) {
                endDay = startDay;
            }
            if (dayRangeContains(startDay, endDay, targetDay)) {
                addTimeRanges(dayMatcher.group(3), windows);
            }
            return;
        }

        addTimeRanges(schedulePart, windows);
    }

    private void addTimeRanges(String text, List<AvailabilityWindow> windows) {
        Matcher matcher = TIME_RANGE_PATTERN.matcher(text);
        while (matcher.find()) {
            LocalTime start = parseTime(matcher.group(1), matcher.group(2));
            LocalTime end = parseTime(matcher.group(3), matcher.group(4));
            if (start.isBefore(end)) {
                windows.add(new AvailabilityWindow(start, end));
            }
        }
    }

    private LocalTime parseTime(String hourValue, String minuteValue) {
        int hour = Integer.parseInt(hourValue);
        int minute = minuteValue == null ? 0 : Integer.parseInt(minuteValue);
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            throw new AppointmentConflictException(
                    "Doctor availability schedule is not configured in a supported format.");
        }
        return LocalTime.of(hour, minute);
    }

    private boolean dayRangeContains(DayOfWeek startDay, DayOfWeek endDay, DayOfWeek targetDay) {
        int day = startDay.getValue();
        while (true) {
            if (DayOfWeek.of(day) == targetDay) {
                return true;
            }
            if (DayOfWeek.of(day) == endDay) {
                return false;
            }
            day = day == 7 ? 1 : day + 1;
        }
    }

    private DayOfWeek dayName(String value) {
        if (value == null) {
            return null;
        }
        return DAY_NAMES.get(value.trim().toLowerCase());
    }

    private static Map<String, DayOfWeek> createDayNameMap() {
        Map<String, DayOfWeek> names = new java.util.HashMap<>();
        names.put("mon", DayOfWeek.MONDAY);
        names.put("monday", DayOfWeek.MONDAY);
        names.put("tue", DayOfWeek.TUESDAY);
        names.put("tues", DayOfWeek.TUESDAY);
        names.put("tuesday", DayOfWeek.TUESDAY);
        names.put("wed", DayOfWeek.WEDNESDAY);
        names.put("wednesday", DayOfWeek.WEDNESDAY);
        names.put("thu", DayOfWeek.THURSDAY);
        names.put("thur", DayOfWeek.THURSDAY);
        names.put("thurs", DayOfWeek.THURSDAY);
        names.put("thursday", DayOfWeek.THURSDAY);
        names.put("fri", DayOfWeek.FRIDAY);
        names.put("friday", DayOfWeek.FRIDAY);
        names.put("sat", DayOfWeek.SATURDAY);
        names.put("saturday", DayOfWeek.SATURDAY);
        names.put("sun", DayOfWeek.SUNDAY);
        names.put("sunday", DayOfWeek.SUNDAY);
        return names;
    }

    private record AvailabilityWindow(LocalTime start, LocalTime end) {
    }

}
