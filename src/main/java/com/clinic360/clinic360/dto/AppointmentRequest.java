package com.clinic360.clinic360.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Long doctorId;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
} 