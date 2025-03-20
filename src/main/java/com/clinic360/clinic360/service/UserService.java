package com.clinic360.clinic360.service;

import com.clinic360.clinic360.dto.DoctorRegistrationRequest;
import com.clinic360.clinic360.dto.PatientRegistrationRequest;
import com.clinic360.clinic360.dto.DoctorProfileUpdateRequest;
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
    List<Patient> getAllPatients();
    List<Patient> getPatientsByDoctorId(Long doctorId);
    User updateUser(User user);
    void updateDoctorProfile(Long doctorId, DoctorProfileUpdateRequest request);
    void removeDoctor(Long doctorId);
    Patient updatePatient(Patient patient);
    
    /**
     * Validates if an email address is valid and has a valid domain
     * @param email the email address to validate
     * @throws RuntimeException if the email is invalid
     */
    void validateEmail(String email);
} 