package com.clinic360.clinic360.config;

import com.clinic360.clinic360.entity.Admin;
import com.clinic360.clinic360.repository.AdminRepository;
import com.clinic360.clinic360.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin if none exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            Admin admin = new Admin(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@clinic360.com",
                    "System",
                    "Administrator"
            );
            
            adminRepository.save(admin);
            log.info("Default admin account created");
        } else {
            log.info("Admin account already exists");
        }
    }
} 