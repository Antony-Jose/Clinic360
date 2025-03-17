package com.clinic360.clinic360.service.impl;

import com.clinic360.clinic360.dto.DoctorRegistrationRequest;
import com.clinic360.clinic360.dto.PatientRegistrationRequest;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.Patient;
import com.clinic360.clinic360.entity.User;
import com.clinic360.clinic360.repository.DoctorRepository;
import com.clinic360.clinic360.repository.PatientRepository;
import com.clinic360.clinic360.repository.UserRepository;
import com.clinic360.clinic360.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerPatient(PatientRegistrationRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create and save the patient
        Patient patient = new Patient(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail(),
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            request.getPhoneNumber(),
            request.getAddress()
        );

        return patientRepository.save(patient);
    }

    @Override
    public Doctor registerDoctor(DoctorRegistrationRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Check if license number already exists
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number is already registered");
        }

        // Create and save the doctor
        Doctor doctor = new Doctor(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail(),
            request.getFirstName(),
            request.getLastName(),
            request.getSpecialization(),
            request.getLicenseNumber(),
            request.getPhoneNumber()
        );

        return doctorRepository.save(doctor);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public Long getUserIdFromUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return user.getId();
    }
    
    @Override
    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
    }
    
    @Override
    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
    }
    
    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    @Override
    public List<Patient> getPatientsByDoctorId(Long doctorId) {
        // This method needs a custom query to find all patients who had appointments with this doctor
        // For now, let's implement a simple version that returns all patients (in reality you'd filter)
        // This will need to be updated with proper query in the repository
        return patientRepository.findDistinctPatientsByDoctorId(doctorId);
    }
    
    @Override
    public User updateUser(User user) {
        // Check if the user exists
        userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + user.getId()));
        
        // Save the updated user
        return userRepository.save(user);
    }
} 