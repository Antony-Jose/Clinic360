package com.clinic360.clinic360.service.impl;

import com.clinic360.clinic360.dto.DoctorRegistrationRequest;
import com.clinic360.clinic360.dto.PatientRegistrationRequest;
import com.clinic360.clinic360.dto.DoctorProfileUpdateRequest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.email.validation.mx-check.enabled:true}")
    private boolean mxCheckEnabled;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Common valid TLDs for additional validation
    private static final Set<String> COMMON_TLDS = new HashSet<>(Arrays.asList(
        "com", "org", "net", "edu", "gov", "mil", "io", "co", "info",
        "biz", "name", "me", "tv", "us", "uk", "ca", "au", "de", "jp",
        "fr", "it", "es", "in", "br", "ru", "nl", "eu", "ch", "se"
    ));

    @Override
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Invalid email format. Please enter a valid email address with a proper domain (e.g., user@example.com)");
        }
        
        if (!email.contains("@") || !email.substring(email.indexOf("@")).contains(".")) {
            throw new RuntimeException("Invalid email format. Email must contain a domain with a dot (e.g., example.com)");
        }
        
        if (email.length() > 100) {
            throw new RuntimeException("Email address is too long");
        }
        
        // Extract domain and TLD
        String domain = email.substring(email.indexOf("@") + 1);
        String tld = domain.substring(domain.lastIndexOf(".") + 1).toLowerCase();
        
        // Check TLD length
        if (tld.length() == 1) {
            throw new RuntimeException("Invalid email domain. Single letter TLDs are not valid");
        }
        
        if (tld.length() > 7) {
            throw new RuntimeException("Invalid email domain. TLD is too long to be valid");
        }
        
        // Optional: Check if TLD is common
        if (!COMMON_TLDS.contains(tld)) {
            // Allow uncommon TLDs but log a warning
            System.out.println("Warning: Uncommon TLD in email: " + email);
        }
        
        // Check if domain has valid MX records
        if (!hasMXRecord(domain)) {
            throw new RuntimeException("Invalid email domain. The domain does not have valid mail servers");
        }
    }
    
    /**
     * Check if a domain has valid MX records for email receiving
     * @param domain the domain to check
     * @return true if domain has MX records, false otherwise
     */
    private boolean hasMXRecord(String domain) {
        // Skip MX validation if disabled in configuration
        if (!mxCheckEnabled) {
            return true;
        }
        
        try {
            // Set up environment for creating initial context
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            
            DirContext dirContext = new InitialDirContext(env);
            Attributes attributes = dirContext.getAttributes(domain, new String[] {"MX"});
            
            return attributes != null && attributes.getAll().hasMoreElements();
        } catch (NamingException e) {
            // If we can't resolve the domain's MX records, assume it's invalid
            return false;
        } catch (Exception e) {
            // If there's any other error, log it but don't fail validation
            // This is a fall-back to ensure the registration process doesn't break
            System.err.println("Error checking MX records for domain: " + domain + ", " + e.getMessage());
            return true;
        }
    }

    @Override
    public User registerPatient(PatientRegistrationRequest request) {
        // Validate email format
        validateEmail(request.getEmail());

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
        // Validate email format
        validateEmail(request.getEmail());

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
        return patientRepository.findDistinctPatientsByDoctorId(doctorId);
    }
    
    @Override
    public List<Patient> searchPatientsByDoctorId(Long doctorId, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getPatientsByDoctorId(doctorId);
        }
        return patientRepository.searchPatientsByDoctorId(doctorId, searchTerm.trim());
    }
    
    @Override
    public User updateUser(User user) {
        // Check if the user exists
        userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + user.getId()));
        
        // Save the updated user
        return userRepository.save(user);
    }

    @Override
    public void updateDoctorProfile(Long doctorId, DoctorProfileUpdateRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Update basic info
        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            doctor.setFirstName(request.getFirstName());
        }
        
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            doctor.setLastName(request.getLastName());
        }
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if email is already in use by another user
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), doctorId)) {
                throw new RuntimeException("Email is already in use");
            }
            doctor.setEmail(request.getEmail());
        }
        
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            doctor.setPhoneNumber(request.getPhoneNumber());
        }
        
        if (request.getSpecialization() != null && !request.getSpecialization().isEmpty()) {
            doctor.setSpecialization(request.getSpecialization());
        }
        
        if (request.getLicenseNumber() != null && !request.getLicenseNumber().isEmpty()) {
            // Check if license number is already in use by another doctor
            if (doctorRepository.existsByLicenseNumberAndIdNot(request.getLicenseNumber(), doctorId)) {
                throw new RuntimeException("License number is already registered");
            }
            doctor.setLicenseNumber(request.getLicenseNumber());
        }
        
        // Password change logic
        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty() &&
            request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            
            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), doctor.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            
            // Verify password confirmation
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new RuntimeException("New passwords do not match");
            }
            
            // Update password
            doctor.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void removeDoctor(Long doctorId) {
        // Check if doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        // Delete the doctor
        doctorRepository.delete(doctor);
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public long getNewPatientsCountThisMonth(Long doctorId) {
        return patientRepository.countNewPatientsThisMonth(doctorId);
    }

    @Override
    @Transactional
    public Patient updatePatient(Patient patient) {
        Patient existingPatient = patientRepository.findById(patient.getId())
            .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patient.getId()));
        
        // Validate email format if it's being updated
        if (!existingPatient.getEmail().equals(patient.getEmail())) {
            validateEmail(patient.getEmail());
            
            // Check if the new email is already in use
            if (userRepository.existsByEmailAndIdNot(patient.getEmail(), patient.getId())) {
                throw new RuntimeException("Email is already in use");
            }
        }
        
        // Update only the allowed fields
        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setEmail(patient.getEmail());
        existingPatient.setPhoneNumber(patient.getPhoneNumber());
        existingPatient.setAddress(patient.getAddress());
        existingPatient.setDateOfBirth(patient.getDateOfBirth());
        
        return patientRepository.save(existingPatient);
    }

    @Override
    @Transactional
    public void changePatientPassword(Long patientId, String currentPassword, String newPassword) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, patient.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Update password
        patient.setPassword(passwordEncoder.encode(newPassword));
        patientRepository.save(patient);
    }

    @Override
    public long getPatientsSeenToday(Long doctorId) {
        return patientRepository.countPatientsSeenToday(doctorId);
    }
} 