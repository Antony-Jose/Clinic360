package com.clinic360.clinic360.service.impl;

import com.clinic360.clinic360.dto.DoctorAvailabilityRequest;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.DoctorAvailability;
import com.clinic360.clinic360.repository.DoctorAvailabilityRepository;
import com.clinic360.clinic360.repository.DoctorRepository;
import com.clinic360.clinic360.service.DoctorAvailabilityService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Override
    @Transactional
    public DoctorAvailability setDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        Optional<DoctorAvailability> existingAvailability = 
                doctorAvailabilityRepository.findByDoctorAndDayOfWeek(doctor, request.getDayOfWeek());
        
        DoctorAvailability availability;
        if (existingAvailability.isPresent()) {
            availability = existingAvailability.get();
            availability.setStartTime(request.getStartTime());
            availability.setEndTime(request.getEndTime());
            availability.setAvailable(request.isAvailable());
        } else {
            availability = new DoctorAvailability();
            availability.setDoctor(doctor);
            availability.setDayOfWeek(request.getDayOfWeek());
            availability.setStartTime(request.getStartTime());
            availability.setEndTime(request.getEndTime());
            availability.setAvailable(request.isAvailable());
        }
        
        return doctorAvailabilityRepository.save(availability);
    }

    @Override
    public List<DoctorAvailability> getDoctorAvailabilities(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        return doctorAvailabilityRepository.findByDoctorOrderByDayOfWeek(doctor);
    }

    @Override
    public DoctorAvailability getDoctorAvailabilityForDay(Long doctorId, DayOfWeek dayOfWeek) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        return doctorAvailabilityRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek)
                .orElse(null);
    }
} 