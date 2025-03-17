package com.clinic360.clinic360.dto;

import com.clinic360.clinic360.entity.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private Long patientId;
    private String patientName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String prescriptionNotes;
    private AppointmentStatus status;
    private java.time.LocalDateTime createdAt;
} 