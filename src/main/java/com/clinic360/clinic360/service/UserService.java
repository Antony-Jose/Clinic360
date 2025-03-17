package com.clinic360.clinic360.service;

import com.clinic360.clinic360.dto.DoctorRegistrationRequest;
import com.clinic360.clinic360.dto.PatientRegistrationRequest;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.Patient;
import com.clinic360.clinic360.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerPatient(PatientRegistrationRequest request);
    Doctor registerDoctor(DoctorRegistrationRequest request);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Additional methods needed for controllers
    Long getUserIdFromUsername(String username);
    Doctor getDoctorById(Long doctorId);
    Patient getPatientById(Long patientId);
    List<Doctor> getAllDoctors();
    List<Patient> getPatientsByDoctorId(Long doctorId);
    User updateUser(User user);
} 