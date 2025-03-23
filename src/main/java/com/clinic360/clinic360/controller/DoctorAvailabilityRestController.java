package com.clinic360.clinic360.controller;

import com.clinic360.clinic360.entity.DoctorAvailability;
import com.clinic360.clinic360.service.DoctorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;

@RestController
@RequestMapping("/api")
public class DoctorAvailabilityRestController {

    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;

    @GetMapping("/doctors/{doctorId}/availability/{dayOfWeek}")
    public ResponseEntity<DoctorAvailability> getDoctorAvailabilityForDay(
            @PathVariable Long doctorId,
            @PathVariable int dayOfWeek) {
        // Convert 0-based day of week to 1-based for DayOfWeek enum
        // Sunday = 0 in JavaScript, but DayOfWeek.SUNDAY = 7 in Java
        DayOfWeek day = dayOfWeek == 0 ? DayOfWeek.SUNDAY : DayOfWeek.of(dayOfWeek);
        
        DoctorAvailability availability = doctorAvailabilityService.getDoctorAvailabilityForDay(doctorId, day);
        if (availability == null) {
            // Return an empty availability object instead of null
            availability = new DoctorAvailability();
            availability.setAvailable(false);
        }
        return ResponseEntity.ok(availability);
    }
} 