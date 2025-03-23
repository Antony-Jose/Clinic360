package com.clinic360.clinic360.service.impl;

import com.clinic360.clinic360.dto.AppointmentRequest;
import com.clinic360.clinic360.dto.AppointmentResponse;
import com.clinic360.clinic360.dto.PrescriptionRequest;
import com.clinic360.clinic360.entity.Appointment;
import com.clinic360.clinic360.entity.AppointmentStatus;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.Patient;
import com.clinic360.clinic360.repository.AppointmentRepository;
import com.clinic360.clinic360.repository.DoctorRepository;
import com.clinic360.clinic360.repository.PatientRepository;
import com.clinic360.clinic360.service.AppointmentService;
import com.clinic360.clinic360.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Appointment bookAppointment(Long patientId, AppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + request.getDoctorId()));
        
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        
        // Check if the time slot is available
        if (!isTimeSlotAvailable(doctor.getId(), request.getAppointmentDate(), request.getStartTime(), request.getEndTime())) {
            throw new RuntimeException("Selected time slot is not available");
        }
        
        // Check if patient doesn't have another appointment at the same time
        if (!canPatientBookAppointment(patientId, request.getAppointmentDate(), request.getStartTime(), request.getEndTime())) {
            throw new RuntimeException("You already have an appointment scheduled at this time");
        }
        
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        List<Appointment> appointments = appointmentRepository.findByDoctorOrderByAppointmentDateDescStartTimeAsc(doctor);
        return convertToResponses(appointments);
    }

    @Override
    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        
        List<Appointment> appointments = appointmentRepository.findByPatientOrderByAppointmentDateDescStartTimeAsc(patient);
        return convertToResponses(appointments);
    }

    @Override
    public List<AppointmentResponse> getUpcomingDoctorAppointments(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        List<Appointment> appointments = appointmentRepository
                .findByDoctorAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(doctor, today);
        
        // Filter today's appointments to only include upcoming ones
        return appointments.stream()
                .filter(appointment -> 
                    !appointment.getAppointmentDate().isEqual(today) || 
                    appointment.getStartTime().isAfter(now))
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.SCHEDULED)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getUpcomingPatientAppointments(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        List<Appointment> appointments = appointmentRepository
                .findByPatientAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(patient, today);
        
        // Filter today's appointments to only include upcoming ones
        return appointments.stream()
                .filter(appointment -> 
                    !appointment.getAppointmentDate().isEqual(today) || 
                    appointment.getStartTime().isAfter(now))
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.SCHEDULED)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));
    }

    @Override
    @Transactional
    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment addPrescription(Long doctorId, PrescriptionRequest request) {
        Appointment appointment = getAppointmentById(request.getAppointmentId());
        
        // Verify that the doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized: You are not the doctor for this appointment");
        }
        
        appointment.setPrescriptionNotes(request.getPrescriptionNotes());
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public boolean cancelAppointment(Long patientId, Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Verify that the patient owns this appointment
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Unauthorized: You are not the patient for this appointment");
        }
        
        // Can only cancel if the appointment is still SCHEDULED
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            return false;
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return true;
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointmentsByDate(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        List<Appointment> appointments = appointmentRepository
                .findByDoctorAndAppointmentDateOrderByStartTimeAsc(doctor, date);
        
        return convertToResponses(appointments);
    }

    @Override
    public boolean isTimeSlotAvailable(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        // Check if there are any appointments that overlap with the requested time slot
        List<Appointment> overlappingAppointments = appointmentRepository
                .findOverlappingAppointments(doctor.getId(), date, startTime, endTime);
        
        return overlappingAppointments.isEmpty();
    }

    @Override
    public boolean canPatientBookAppointment(Long patientId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        
        // Check if there are any appointments that overlap with the requested time slot
        List<Appointment> overlappingAppointments = appointmentRepository
                .findPatientOverlappingAppointments(patient.getId(), date, startTime, endTime);
        
        return overlappingAppointments.isEmpty();
    }
    
    private List<AppointmentResponse> convertToResponses(List<Appointment> appointments) {
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private AppointmentResponse convertToResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());
        response.setStatus(appointment.getStatus());
        response.setPrescriptionNotes(appointment.getPrescriptionNotes());
        return response;
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointmentsForCurrentWeek(Long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDoctorAppointmentsForCurrentWeek'");
    }

    @Override
    public Object getPendingPrescriptionsCount(Long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPendingPrescriptionsCount'");
    }

    @Override
    public List<Appointment> getUpcomingAppointmentsByDoctorId(Long doctorId) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByDoctorIdAndAppointmentDateGreaterThanEqualAndStatusNotOrderByAppointmentDateAscStartTimeAsc(
            doctorId, today, AppointmentStatus.CANCELLED);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
} 