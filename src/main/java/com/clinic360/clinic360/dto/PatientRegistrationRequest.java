package com.clinic360.clinic360.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientRegistrationRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
} 