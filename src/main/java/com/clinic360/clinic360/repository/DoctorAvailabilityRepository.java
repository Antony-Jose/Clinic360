package com.clinic360.clinic360.repository;

import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorOrderByDayOfWeek(Doctor doctor);
    
    Optional<DoctorAvailability> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
    
    List<DoctorAvailability> findByDoctorAndIsAvailableTrue(Doctor doctor);
} 