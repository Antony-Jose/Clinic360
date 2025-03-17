package com.clinic360.clinic360.dto;

import lombok.Data;

@Data
public class PrescriptionRequest {
    private Long appointmentId;
    private String prescriptionNotes;
} 