package com.clinic360.clinic360.repository;

import com.clinic360.clinic360.entity.Appointment;
import com.clinic360.clinic360.entity.AppointmentStatus;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorOrderByAppointmentDateAscStartTimeAsc(Doctor doctor);
    
    List<Appointment> findByPatientOrderByAppointmentDateAscStartTimeAsc(Patient patient);
    
    List<Appointment> findByDoctorOrderByAppointmentDateDescStartTimeAsc(Doctor doctor);
    
    List<Appointment> findByPatientOrderByAppointmentDateDescStartTimeAsc(Patient patient);
    
    List<Appointment> findByDoctorAndStatusOrderByAppointmentDateAscStartTimeAsc(Doctor doctor, AppointmentStatus status);
    
    List<Appointment> findByPatientAndStatusOrderByAppointmentDateAscStartTimeAsc(Patient patient, AppointmentStatus status);
    
    List<Appointment> findByDoctorAndAppointmentDateOrderByStartTimeAsc(Doctor doctor, LocalDate date);
    
    List<Appointment> findByDoctorAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(Doctor doctor, LocalDate date);
    
    List<Appointment> findByPatientAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(Patient patient, LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDate = :date " +
           "AND (a.startTime = :startTime AND a.endTime = :endTime) " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findOverlappingAppointmentsForPatient(
            Long patientId, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date " +
           "AND (a.startTime = :startTime AND a.endTime = :endTime) " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findOverlappingAppointmentsForDoctor(
            Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date " +
           "AND (a.startTime = :startTime AND a.endTime = :endTime) " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findOverlappingAppointments(
            Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDate = :date " +
           "AND (a.startTime = :startTime AND a.endTime = :endTime) " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findPatientOverlappingAppointments(
            Long patientId, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate >= :today " +
           "AND a.status = 'SCHEDULED' ORDER BY a.appointmentDate, a.startTime")
    List<Appointment> findUpcomingAppointmentsForDoctor(Doctor doctor, LocalDate today);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.appointmentDate >= :today " +
           "AND a.status = 'SCHEDULED' ORDER BY a.appointmentDate, a.startTime")
    List<Appointment> findUpcomingAppointmentsForPatient(Patient patient, LocalDate today);

    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorIdAndAppointmentDateGreaterThanEqualAndStatusNotOrderByAppointmentDateAscStartTimeAsc(
        Long doctorId, LocalDate date, AppointmentStatus status);
} 