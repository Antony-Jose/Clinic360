package com.clinic360.clinic360.service.impl;

import com.clinic360.clinic360.dto.DoctorAvailabilityRequest;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.DoctorAvailability;
import com.clinic360.clinic360.entity.TimeSlot;
import com.clinic360.clinic360.repository.DoctorAvailabilityRepository;
import com.clinic360.clinic360.repository.DoctorRepository;
import com.clinic360.clinic360.repository.TimeSlotRepository;
import com.clinic360.clinic360.service.DoctorAvailabilityService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final TimeSlotRepository timeSlotRepository;
    
    private static final int SLOT_DURATION_MINUTES = 15;

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
        
        availability = doctorAvailabilityRepository.save(availability);
        
        // Generate time slots based on the availability
        if (availability.isAvailable()) {
            generateTimeSlotsForDay(doctorId, request.getDayOfWeek());
        } else {
            // Delete existing time slots for this day
            List<TimeSlot> existingSlots = timeSlotRepository.findByDoctorAndDayOfWeek(doctor, request.getDayOfWeek());
            timeSlotRepository.deleteAll(existingSlots);
        }
        
        return availability;
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

    @Override
    @Transactional
    public void generateTimeSlots(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        List<DoctorAvailability> availabilities = 
                doctorAvailabilityRepository.findByDoctorAndIsAvailableTrue(doctor);
        
        for (DoctorAvailability availability : availabilities) {
            generateTimeSlotsForAvailability(doctor, availability);
        }
    }

    @Override
    @Transactional
    public void generateTimeSlotsForDay(Long doctorId, DayOfWeek dayOfWeek) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        Optional<DoctorAvailability> availabilityOpt = 
                doctorAvailabilityRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
        
        if (availabilityOpt.isPresent() && availabilityOpt.get().isAvailable()) {
            // Delete existing time slots for this day
            List<TimeSlot> existingSlots = timeSlotRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
            timeSlotRepository.deleteAll(existingSlots);
            
            // Generate new time slots
            generateTimeSlotsForAvailability(doctor, availabilityOpt.get());
        }
    }
    
    @Override
    public List<TimeSlot> getDoctorTimeSlotsForDate(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        
        // Get day of week from the date
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        // Get availability for this day of week
        Optional<DoctorAvailability> availabilityOpt = 
                doctorAvailabilityRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
        
        // If no availability is set or doctor is not available on this day, return empty list
        if (availabilityOpt.isEmpty() || !availabilityOpt.get().isAvailable()) {
            return List.of();
        }
        
        // Get time slots for this day of week
        List<TimeSlot> timeSlots = timeSlotRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
        
        // Filter out slots that are already booked for this specific date
        // This would require checking against appointments for this date
        // For now, we're just returning all defined slots for the day of week
        return timeSlots;
    }
    
    private void generateTimeSlotsForAvailability(Doctor doctor, DoctorAvailability availability) {
        LocalTime startTime = availability.getStartTime();
        LocalTime endTime = availability.getEndTime();
        
        List<TimeSlot> timeSlots = new ArrayList<>();
        
        LocalTime currentStart = startTime;
        while (currentStart.plus(Duration.ofMinutes(SLOT_DURATION_MINUTES)).compareTo(endTime) <= 0) {
            LocalTime currentEnd = currentStart.plus(Duration.ofMinutes(SLOT_DURATION_MINUTES));
            
            TimeSlot slot = new TimeSlot();
            slot.setDoctor(doctor);
            slot.setDayOfWeek(availability.getDayOfWeek());
            slot.setStartTime(currentStart);
            slot.setEndTime(currentEnd);
            slot.setAvailable(true);
            
            timeSlots.add(slot);
            
            currentStart = currentEnd;
        }
        
        timeSlotRepository.saveAll(timeSlots);
    }
} 