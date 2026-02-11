package com.proiect.appointment_booking_system.config;

import com.proiect.appointment_booking_system.enums.Role;
import com.proiect.appointment_booking_system.enums.Status;
import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.Patient;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.AppointmentRepository;
import com.proiect.appointment_booking_system.repository.ClinicRepository;
import com.proiect.appointment_booking_system.repository.DoctorRepository;
import com.proiect.appointment_booking_system.repository.PatientRepository;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.seed.demo-data", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(
            UserRepository userRepository,
            ClinicRepository clinicRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Clinic clinicOne = upsertClinic(
                "Heart Care Central",
                "Bucharest, Sector 1",
                "+40211234567",
                "heartcare@demo.local");
        Clinic clinicTwo = upsertClinic(
                "City Medical Hub",
                "Bucharest, Sector 3",
                "+40217654321",
                "cityhub@demo.local");

        Doctor doctorDoe = upsertDoctor(
                "Dr. John Doe",
                "doctor.doe@demo.local",
                "+40711100001",
                "Demo1234!",
                "Cardiologist",
                "Monday: 09:00-12:00, 14:00-17:00; Tuesday: 09:00-12:00",
                Set.of(clinicOne, clinicTwo));
        Doctor doctorSmith = upsertDoctor(
                "Dr. John Smith",
                "doctor.smith@demo.local",
                "+40711100002",
                "Demo1234!",
                "Cardiologist",
                "Monday: 09:00-12:00, 14:00-17:00; Tuesday: 09:00-12:00",
                Set.of(clinicOne));

        Patient patientAna = upsertPatient(
                "Ana Popescu",
                "ana.popescu@demo.local",
                "+40722200001",
                "Demo1234!",
                "Bucharest, Sector 2",
                "No chronic conditions.");
        Patient patientMihai = upsertPatient(
                "Mihai Ionescu",
                "mihai.ionescu@demo.local",
                "+40722200002",
                "Demo1234!",
                "Bucharest, Sector 6",
                "Seasonal allergies.");

        LocalDate baseDate = LocalDate.now().plusDays(2);
        createAppointmentIfAbsent(patientAna, doctorDoe, clinicOne, baseDate, LocalTime.of(10, 0), 30);
        createAppointmentIfAbsent(patientMihai, doctorDoe, clinicTwo, baseDate, LocalTime.of(11, 0), 45);
        createAppointmentIfAbsent(patientAna, doctorSmith, clinicOne, baseDate.plusDays(1), LocalTime.of(10, 0), 60);

        log.info("Demo seed complete. Demo users created/updated for doctor and patient login.");
    }

    private Clinic upsertClinic(String name, String location, String contactNumber, String email) {
        Clinic clinic = clinicRepository.findByEmail(email).orElseGet(Clinic::new);
        clinic.setName(name);
        clinic.setLocation(location);
        clinic.setContactNumber(contactNumber);
        clinic.setEmail(email);
        return clinicRepository.save(clinic);
    }

    private Doctor upsertDoctor(
            String name,
            String email,
            String phone,
            String rawPassword,
            String specialization,
            String availabilitySchedule,
            Set<Clinic> clinics) {
        User user = upsertUser(name, email, phone, rawPassword, Role.DOCTOR);
        Doctor doctor = doctorRepository.findById(user.getId()).orElseGet(Doctor::new);
        doctor.setId(user.getId());
        doctor.setUser(user);
        doctor.setSpecialization(specialization);
        doctor.setAvailabilitySchedule(availabilitySchedule);
        doctor.setClinics(clinics);
        return doctorRepository.save(doctor);
    }

    private Patient upsertPatient(
            String name,
            String email,
            String phone,
            String rawPassword,
            String address,
            String medicalHistory) {
        User user = upsertUser(name, email, phone, rawPassword, Role.PATIENT);
        Patient patient = patientRepository.findById(user.getId()).orElseGet(Patient::new);
        patient.setId(user.getId());
        patient.setUser(user);
        patient.setAddress(address);
        patient.setMedicalHistory(medicalHistory);
        return patientRepository.save(patient);
    }

    private User upsertUser(String name, String email, String phone, String rawPassword, Role role) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    private void createAppointmentIfAbsent(
            Patient patient,
            Doctor doctor,
            Clinic clinic,
            LocalDate date,
            LocalTime time,
            int durationMinutes) {
        LocalDateTime requestedStart = date.atTime(time);
        LocalDateTime requestedEnd = requestedStart.plusMinutes(durationMinutes);

        List<Appointment> doctorAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndStatusNot(doctor.getId(), date, Status.CANCELLED);
        boolean doctorBusy = doctorAppointments.stream()
                .anyMatch(existing -> overlaps(existing, requestedStart, requestedEnd));

        List<Appointment> patientAppointments = appointmentRepository
                .findByPatientIdAndAppointmentDateAndStatusNot(patient.getId(), date, Status.CANCELLED);
        boolean patientBusy = patientAppointments.stream()
                .anyMatch(existing -> overlaps(existing, requestedStart, requestedEnd));

        if (doctorBusy || patientBusy) {
            return;
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setDurationMinutes(durationMinutes);
        appointment.setStatus(Status.BOOKED);
        appointmentRepository.save(appointment);
    }

    private boolean overlaps(Appointment existing, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        if (existing.getAppointmentDate() == null || existing.getAppointmentTime() == null) {
            return false;
        }
        int existingDuration = existing.getDurationMinutes() == null ? 30 : existing.getDurationMinutes();
        LocalDateTime existingStart = existing.getAppointmentDate().atTime(existing.getAppointmentTime());
        LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);
        return requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart);
    }
}
