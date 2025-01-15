package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.dto.NotificationDTO;
import com.proiect.appointment_booking_system.enums.Status;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
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

    public void createAppointment(AppointmentDTO dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId()).orElseThrow(ClinicNotFound::new);
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(PatientNotFound::new);
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow(DoctorNotFound::new);

        Appointment appointment = AppointmentMapper.toEntity(dto, patient, doctor, clinic);
        Appointment savedAppointment = repository.save(appointment);

        LocalDateTime notificationTime = appointment.getAppointmentDate().atTime(appointment.getAppointmentTime())
                .minusDays(1);
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

}
