package com.clinic360.clinic360.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Doctor extends User {
    
    @Column(name = "specialization")
    private String specialization;
    
    @Column(name = "license_number", unique = true)
    private String licenseNumber;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    // Constructor to set role automatically
    public Doctor(String username, String password, String email, String firstName, String lastName, 
                 String specialization, String licenseNumber, String phoneNumber) {
        super();
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(Role.DOCTOR);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
    }
} 