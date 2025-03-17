package com.clinic360.clinic360.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class DoctorAvailabilityRequest {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
} 