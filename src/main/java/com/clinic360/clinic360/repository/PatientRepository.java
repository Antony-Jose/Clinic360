package com.clinic360.clinic360.repository;

import com.clinic360.clinic360.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    @Query("SELECT DISTINCT p FROM Patient p JOIN Appointment a ON a.patient = p WHERE a.doctor.id = :doctorId")
    List<Patient> findDistinctPatientsByDoctorId(Long doctorId);

    @Query("SELECT DISTINCT p FROM Patient p JOIN Appointment a ON a.patient = p " +
           "WHERE a.doctor.id = :doctorId " +
           "AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Patient> searchPatientsByDoctorId(Long doctorId, String searchTerm);

    @Query("SELECT COUNT(DISTINCT p) FROM Patient p JOIN Appointment a ON a.patient = p " +
           "WHERE a.doctor.id = :doctorId " +
           "AND MONTH(a.createdAt) = MONTH(CURRENT_DATE()) " +
           "AND YEAR(a.createdAt) = YEAR(CURRENT_DATE())")
    long countNewPatientsThisMonth(Long doctorId);

    @Query("SELECT COUNT(DISTINCT p) FROM Patient p JOIN Appointment a ON a.patient = p " +
           "WHERE a.doctor.id = :doctorId " +
           "AND DATE(a.appointmentDate) = CURRENT_DATE " +
           "AND a.status = 'COMPLETED'")
    long countPatientsSeenToday(Long doctorId);
} 