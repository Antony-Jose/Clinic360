package com.clinic360.clinic360.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Admin extends User {
    
    // Constructor to set role automatically
    public Admin(String username, String password, String email, String firstName, String lastName) {
        super();
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(Role.ADMIN);
    }
} 