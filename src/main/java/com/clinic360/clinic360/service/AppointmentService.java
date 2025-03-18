package com.clinic360.clinic360.service;

import com.clinic360.clinic360.dto.AppointmentRequest;
import com.clinic360.clinic360.dto.AppointmentResponse;
import com.clinic360.clinic360.dto.PrescriptionRequest;
import com.clinic360.clinic360.entity.Appointment;
import com.clinic360.clinic360.entity.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    
    Appointment bookAppointment(Long patientId, AppointmentRequest request);
    
    List<AppointmentResponse> getDoctorAppointments(Long doctorId);
    
    List<AppointmentResponse> getPatientAppointments(Long patientId);
    
    List<AppointmentResponse> getUpcomingDoctorAppointments(Long doctorId);
    
    List<AppointmentResponse> getUpcomingPatientAppointments(Long patientId);
    
    Appointment getAppointmentById(Long appointmentId);
    
    Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status);
    
    Appointment addPrescription(Long doctorId, PrescriptionRequest request);
    
    boolean cancelAppointment(Long patientId, Long appointmentId);
    
    List<AppointmentResponse> getDoctorAppointmentsByDate(Long doctorId, LocalDate date);
    
    boolean isTimeSlotAvailable(Long doctorId, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime);
    
    boolean canPatientBookAppointment(Long patientId, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime);

    List<AppointmentResponse> getDoctorAppointmentsForCurrentWeek(Long doctorId);

    Object getPendingPrescriptionsCount(Long doctorId);

    List<Appointment> getUpcomingAppointmentsByDoctorId(Long doctorId);

    List<Appointment> getAllAppointments();
} 