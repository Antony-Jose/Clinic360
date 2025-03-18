package com.clinic360.clinic360.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic360.clinic360.entity.DatabaseConnectionTest;

/**
 * Service to test database connectivity on application startup.
 */
@Service
@Slf4j
public class DatabaseService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Tests database connectivity when the application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void testDatabaseConnection() {
        try {
            // Try to execute a simple query
            Object result = entityManager.createNativeQuery("SELECT 1").getSingleResult();
            
            // If successful, save a test entity
            DatabaseConnectionTest test = new DatabaseConnectionTest();
            test.setMessage("Database connection successful");
            test.setConnectionSuccessful(true);
            entityManager.persist(test);
            
            log.info("✅ Database connection test successful!");
        } catch (Exception e) {
            log.error("❌ Database connection test failed: {}", e.getMessage(), e);
        }
    }
}