package com.proiect.appointment_booking_system.mapper;

import com.proiect.appointment_booking_system.dto.AppointmentDTO;
import com.proiect.appointment_booking_system.model.Appointment;
import com.proiect.appointment_booking_system.model.Clinic;
import com.proiect.appointment_booking_system.model.Doctor;
import com.proiect.appointment_booking_system.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    public AppointmentDTO toDTO(Appointment appointment) {
        AppointmentDTO appointmentDto = new AppointmentDTO();
        appointmentDto.setId(appointment.getId());
        appointmentDto.setPatientId(appointment.getPatient().getId());
        appointmentDto.setDoctorId(appointment.getDoctor().getId());
        appointmentDto.setClinicId(appointment.getClinic().getId());
        appointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        appointmentDto.setAppointmentTime(appointment.getAppointmentTime());
        appointmentDto.setStatus(appointment.getStatus().toString());
        return appointmentDto;
    }

    public Appointment toEntity(AppointmentDTO appointmentDto, Patient patient, Doctor doctor, Clinic clinic) {
        Appointment appointment = new Appointment();
        appointment.setId(appointmentDto.getId());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);
        appointment.setAppointmentDate(appointmentDto.getAppointmentDate());
        appointment.setAppointmentTime(appointmentDto.getAppointmentTime());
        appointment.setStatus(appointmentDto.getStatus());
        return appointment;
    }
}
