package com.clinic360.clinic360.controller;

import com.clinic360.clinic360.dto.AppointmentResponse;
import com.clinic360.clinic360.dto.DoctorAvailabilityRequest;
import com.clinic360.clinic360.dto.DoctorProfileUpdateRequest;
import com.clinic360.clinic360.dto.PrescriptionRequest;
import com.clinic360.clinic360.entity.Appointment;
import com.clinic360.clinic360.entity.AppointmentStatus;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.DoctorAvailability;
import com.clinic360.clinic360.entity.Patient;
import com.clinic360.clinic360.service.AppointmentService;
import com.clinic360.clinic360.service.DoctorAvailabilityService;
import com.clinic360.clinic360.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    
    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            Long doctorId = userService.getUserIdFromUsername(authentication.getName());
            
            // Get doctor details
            Doctor doctor = userService.getDoctorById(doctorId);
            model.addAttribute("doctor", doctor);
            
            // Add placeholder data for statistics
            model.addAttribute("todayAppointments", Collections.emptyList());
            model.addAttribute("appointmentsToday", 0);
            model.addAttribute("appointmentsThisWeek", 0);
            model.addAttribute("upcomingAppointments", Collections.emptyList());
            model.addAttribute("totalPatients", 0);
            model.addAttribute("pendingPrescriptions", 0);
            model.addAttribute("recentNotifications", Collections.emptyList());
            
            return "doctor/dashboard";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            return "redirect:/login?error=true";
        }
    }
    
    @GetMapping("/availability")
    public String availabilityPage(Authentication authentication, Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        List<DoctorAvailability> availabilities = doctorAvailabilityService.getDoctorAvailabilities(doctorId);
        model.addAttribute("availabilities", availabilities);
        model.addAttribute("daysOfWeek", DayOfWeek.values());
        return "doctor/availability";
    }
    
    @PostMapping("/availability/set")
    public String setAvailability(Authentication authentication, 
                                  @ModelAttribute DoctorAvailabilityRequest request,
                                  RedirectAttributes redirectAttributes) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        try {
            DoctorAvailability availability = doctorAvailabilityService.setDoctorAvailability(doctorId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Availability updated successfully for " + request.getDayOfWeek());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update availability: " + e.getMessage());
        }
        return "redirect:/doctor/availability";
    }
    
    @GetMapping("/appointments")
    public String viewAppointments(Authentication authentication, Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        List<AppointmentResponse> appointments = appointmentService.getDoctorAppointments(doctorId);
        model.addAttribute("appointments", appointments);
        return "doctor/appointments";
    }
    
    @GetMapping("/appointments/date")
    public String viewAppointmentsByDate(Authentication authentication,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        List<AppointmentResponse> appointments = appointmentService.getDoctorAppointmentsByDate(doctorId, date);
        model.addAttribute("appointments", appointments);
        model.addAttribute("selectedDate", date);
        return "doctor/appointments-by-date";
    }
    
    @GetMapping("/appointments/{id}")
    public String viewAppointmentDetail(Authentication authentication,
                                        @PathVariable("id") Long appointmentId,
                                        Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        // Security check to ensure the doctor can only view their own appointments
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            return "redirect:/doctor/appointments?error=unauthorized";
        }
        
        model.addAttribute("appointment", appointment);
        return "doctor/appointment-detail";
    }
    
    @PostMapping("/appointments/{id}/prescription")
    public String addPrescription(Authentication authentication,
                                  @PathVariable("id") Long appointmentId,
                                  @ModelAttribute PrescriptionRequest request,
                                  RedirectAttributes redirectAttributes) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        try {
            request.setAppointmentId(appointmentId);
            Appointment appointment = appointmentService.addPrescription(doctorId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add prescription: " + e.getMessage());
        }
        return "redirect:/doctor/appointments/" + appointmentId;
    }
    
    @GetMapping("/patients")
    public String viewPatients(Authentication authentication, 
                             @RequestParam(required = false) String search,
                             Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        List<Patient> patients;
        
        if (search != null && !search.trim().isEmpty()) {
            patients = userService.searchPatientsByDoctorId(doctorId, search);
        } else {
            patients = userService.getPatientsByDoctorId(doctorId);
        }
        
        model.addAttribute("patients", patients);
        model.addAttribute("searchTerm", search);
        
        // Add patient statistics
        model.addAttribute("totalPatients", patients.size());
        model.addAttribute("newPatientsThisMonth", userService.getNewPatientsCountThisMonth(doctorId));
        model.addAttribute("patientsWithAppointments", patients.size());  // All patients have at least one appointment
        model.addAttribute("patientsSeenToday", userService.getPatientsSeenToday(doctorId));
        
        // Add empty lists for recent patients
        model.addAttribute("recentPatients", Collections.emptyList());
        
        // Add pagination attributes
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);
        
        return "doctor/patients";
    }
    
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        model.addAttribute("doctor", userService.getDoctorById(doctorId));
        
        // Add an empty object for profile updates
        model.addAttribute("profileUpdateRequest", new DoctorProfileUpdateRequest());
        
        return "doctor/profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(
            Authentication authentication,
            @ModelAttribute("profileUpdateRequest") DoctorProfileUpdateRequest request,
            RedirectAttributes redirectAttributes) {
        
        try {
            Long doctorId = userService.getUserIdFromUsername(authentication.getName());
            userService.updateDoctorProfile(doctorId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/doctor/profile";
    }
    
    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(Authentication authentication,
                                         @PathVariable("id") Long appointmentId,
                                         @RequestParam("status") String statusValue,
                                         RedirectAttributes redirectAttributes) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        try {
            // Get the appointment first to verify ownership
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            
            // Security check to ensure the doctor can only update their own appointments
            if (!appointment.getDoctor().getId().equals(doctorId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized: You can only update your own appointments");
                return "redirect:/doctor/appointments";
            }
            
            // Convert string status to enum
            AppointmentStatus status = AppointmentStatus.valueOf(statusValue);
            
            // Update the status
            appointmentService.updateAppointmentStatus(appointmentId, status);
            
            // Add success message
            redirectAttributes.addFlashAttribute("successMessage", 
                "Appointment " + (status == AppointmentStatus.COMPLETED ? "marked as completed" : "cancelled") + " successfully");
            
            return "redirect:/doctor/appointments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update appointment status: " + e.getMessage());
            return "redirect:/doctor/appointments/" + appointmentId;
        }
    }
} 