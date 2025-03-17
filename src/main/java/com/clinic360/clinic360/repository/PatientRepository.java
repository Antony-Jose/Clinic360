package com.clinic360.clinic360.repository;

import com.clinic360.clinic360.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    @Query("SELECT DISTINCT p FROM Patient p JOIN Appointment a ON a.patient = p WHERE a.doctor.id = :doctorId")
    List<Patient> findDistinctPatientsByDoctorId(Long doctorId);
} 