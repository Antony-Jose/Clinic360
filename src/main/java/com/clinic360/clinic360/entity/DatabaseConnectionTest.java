package com.clinic360.clinic360.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simple entity to test the database connection.
 * You can delete this class after verifying that your database configuration works.
 */
@Entity
@Table(name = "database_connection_test")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConnectionTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String message;
    
    private boolean connectionSuccessful;
} 