package com.clinic360.clinic360.controller;

import com.clinic360.clinic360.dto.AppointmentRequest;
import com.clinic360.clinic360.dto.AppointmentResponse;
import com.clinic360.clinic360.entity.Appointment;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.Patient;
import com.clinic360.clinic360.entity.TimeSlot;
import com.clinic360.clinic360.service.AppointmentService;
import com.clinic360.clinic360.service.DoctorAvailabilityService;
import com.clinic360.clinic360.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Long patientId = userService.getUserIdFromUsername(authentication.getName());
        List<AppointmentResponse> upcomingAppointments = appointmentService.getUpcomingPatientAppointments(patientId);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        return "patient/dashboard";
    }
    
    @GetMapping("/appointments")
    public String viewAppointments(Authentication authentication, Model model) {
        Long patientId = userService.getUserIdFromUsername(authentication.getName());
        List<AppointmentResponse> appointments = appointmentService.getPatientAppointments(patientId);
        model.addAttribute("appointments", appointments);
        return "patient/appointments";
    }
    
    @GetMapping("/book-appointment")
    public String bookAppointmentForm(Model model) {
        List<Doctor> doctors = userService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointmentRequest", new AppointmentRequest());
        return "patient/book-appointment";
    }
    
    @GetMapping("/doctor-slots")
    @ResponseBody
    public List<TimeSlot> getDoctorSlots(@RequestParam Long doctorId, 
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return doctorAvailabilityService.getDoctorTimeSlotsForDate(doctorId, date);
    }
    
    @PostMapping("/book-appointment")
    public String bookAppointment(Authentication authentication,
                                 @ModelAttribute AppointmentRequest request,
                                 RedirectAttributes redirectAttributes) {
        Long patientId = userService.getUserIdFromUsername(authentication.getName());
        System.out.println("Patient ID: " + request);
        try {
            // Check if patient is already booked at the same time with another doctor
            if (!appointmentService.canPatientBookAppointment(patientId, request.getAppointmentDate(), 
                                                             request.getStartTime(), request.getEndTime())) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "You already have an appointment scheduled at this time. Please choose a different time.");
                return "redirect:/patient/book-appointment";
            }
                
            Appointment appointment = appointmentService.bookAppointment(patientId, request);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Appointment booked successfully for " + request.getAppointmentDate() + " at " + request.getStartTime());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to book appointment: " + e.getMessage());
        }

        return "redirect:/patient/appointments";
    }
    
    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(Authentication authentication,
                                   @PathVariable("id") Long appointmentId,
                                   RedirectAttributes redirectAttributes) {
        Long patientId = userService.getUserIdFromUsername(authentication.getName());
        try {
            boolean canceled = appointmentService.cancelAppointment(patientId, appointmentId);
            if (canceled) {
                redirectAttributes.addFlashAttribute("successMessage", "Appointment canceled successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel appointment");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel appointment: " + e.getMessage());
        }
        return "redirect:/patient/appointments";
    }
    
    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        Long patientId = userService.getUserIdFromUsername(authentication.getName());
        model.addAttribute("patient", userService.getPatientById(patientId));
        return "patient/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
                              @ModelAttribute("patient") Patient patient,
                              RedirectAttributes redirectAttributes) {
        try {
            Long patientId = userService.getUserIdFromUsername(authentication.getName());
            patient.setId(patientId); // Ensure we're updating the correct patient
            userService.updatePatient(patient);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/patient/profile";
    }
} 