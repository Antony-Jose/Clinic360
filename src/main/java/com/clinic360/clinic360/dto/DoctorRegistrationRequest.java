package com.clinic360.clinic360.dto;

import lombok.Data;

@Data
public class DoctorRegistrationRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String firstName;
    private String lastName;
    private String specialization;
    private String licenseNumber;
    private String phoneNumber;
} 