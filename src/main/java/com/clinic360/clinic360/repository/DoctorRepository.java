package com.clinic360.clinic360.repository;

import com.clinic360.clinic360.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    boolean existsByLicenseNumber(String licenseNumber);
} 