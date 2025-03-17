package com.clinic360.clinic360.service;

import com.clinic360.clinic360.dto.DoctorAvailabilityRequest;
import com.clinic360.clinic360.entity.DoctorAvailability;
import com.clinic360.clinic360.entity.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface DoctorAvailabilityService {
    DoctorAvailability setDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request);
    
    List<DoctorAvailability> getDoctorAvailabilities(Long doctorId);
    
    DoctorAvailability getDoctorAvailabilityForDay(Long doctorId, DayOfWeek dayOfWeek);
    
    void generateTimeSlots(Long doctorId);
    
    void generateTimeSlotsForDay(Long doctorId, DayOfWeek dayOfWeek);
    
    List<TimeSlot> getDoctorTimeSlotsForDate(Long doctorId, LocalDate date);
} 