package com.clinic360.clinic360.controller;

import com.clinic360.clinic360.dto.DoctorRegistrationRequest;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.entity.DoctorAvailability;
import com.clinic360.clinic360.entity.Appointment;
import com.clinic360.clinic360.service.UserService;
import com.clinic360.clinic360.service.AppointmentService;
import com.clinic360.clinic360.service.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final DoctorAvailabilityService doctorAvailabilityService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Get counts
            long doctorCount = userService.getAllDoctors().size();
            long patientCount = userService.getAllPatients().size();
            long appointmentCount = appointmentService.getAllAppointments().size();
            
            model.addAttribute("doctorCount", doctorCount);
            model.addAttribute("patientCount", patientCount);
            model.addAttribute("appointmentCount", appointmentCount);
            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    @GetMapping("/register-doctor")
    public String registerDoctorPage(Model model) {
        model.addAttribute("doctor", new DoctorRegistrationRequest());
        return "admin/register-doctor";
    }

    @PostMapping("/register-doctor")
    public String registerDoctor(@ModelAttribute("doctor") DoctorRegistrationRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            // Validate passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/admin/register-doctor";
            }

            // Register the doctor
            Doctor doctor = userService.registerDoctor(request);
            redirectAttributes.addFlashAttribute("success", "Doctor registered successfully!");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/register-doctor";
        }
    }
    
    @GetMapping("/doctors")
    public String doctorsPage(Model model) {
        try {
            List<Doctor> doctors = userService.getAllDoctors();
            
            // For each doctor, fetch their availabilities and upcoming appointments
            for (Doctor doctor : doctors) {
                List<DoctorAvailability> availabilities = doctorAvailabilityService.getDoctorAvailabilities(doctor.getId());
                List<Appointment> appointments = appointmentService.getUpcomingAppointmentsByDoctorId(doctor.getId());
                
                doctor.setAvailabilities(availabilities);
                doctor.setAppointments(appointments);
            }
            
            model.addAttribute("doctors", doctors);
            return "admin/doctors";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading doctors: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }
    
    @PostMapping("/doctors/{id}/remove")
    public String removeDoctor(@PathVariable("id") Long doctorId, RedirectAttributes redirectAttributes) {
        try {
            userService.removeDoctor(doctorId);
            redirectAttributes.addFlashAttribute("success", "Doctor removed successfully");
            return "redirect:/admin/doctors";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove doctor: " + e.getMessage());
            return "redirect:/admin/doctors";
        }
    }
    
    @GetMapping("/doctors/{id}/schedule/manage")
    public String manageDoctorSchedulePage(@PathVariable("id") Long doctorId, Model model) {
        try {
            Doctor doctor = userService.getDoctorById(doctorId);
            List<DoctorAvailability> availabilities = doctorAvailabilityService.getDoctorAvailabilities(doctorId);
            
            model.addAttribute("doctor", doctor);
            model.addAttribute("availabilities", availabilities);
            return "admin/manage-doctor-schedule";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading doctor schedule: " + e.getMessage());
            return "redirect:/admin/doctors";
        }
    }
    
    @GetMapping("/doctors/{id}/appointments")
    public String scheduleDoctorAppointmentPage(@PathVariable("id") Long doctorId, Model model) {
        try {
            Doctor doctor = userService.getDoctorById(doctorId);
            List<DoctorAvailability> availabilities = doctorAvailabilityService.getDoctorAvailabilities(doctorId);
            
            model.addAttribute("doctor", doctor);
            model.addAttribute("availabilities", availabilities);
            return "admin/schedule-appointment";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading appointment scheduling page: " + e.getMessage());
            return "redirect:/admin/doctors";
        }
    }
} 