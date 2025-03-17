package com.clinic360.clinic360.controller;

import com.clinic360.clinic360.dto.AppointmentResponse;
import com.clinic360.clinic360.dto.DoctorAvailabilityRequest;
import com.clinic360.clinic360.dto.PrescriptionRequest;
import com.clinic360.clinic360.entity.Appointment;
import com.clinic360.clinic360.entity.DoctorAvailability;
import com.clinic360.clinic360.entity.Patient;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
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
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        List<AppointmentResponse> upcomingAppointments = appointmentService.getUpcomingDoctorAppointments(doctorId);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        return "doctor/dashboard";
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
            doctorAvailabilityService.generateTimeSlotsForDay(doctorId, request.getDayOfWeek());
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
    public String viewPatients(Authentication authentication, Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        List<Patient> patients = userService.getPatientsByDoctorId(doctorId);
        model.addAttribute("patients", patients);
        return "doctor/patients";
    }
    
    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        Long doctorId = userService.getUserIdFromUsername(authentication.getName());
        model.addAttribute("doctor", userService.getDoctorById(doctorId));
        return "doctor/profile";
    }
} 