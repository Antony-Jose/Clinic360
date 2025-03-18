package com.clinic360.clinic360.dto;

import lombok.Data;

@Data
public class DoctorProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String licenseNumber;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
} 