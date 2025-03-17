package com.clinic360.clinic360.controller;

import com.clinic360.clinic360.dto.DoctorRegistrationRequest;
import com.clinic360.clinic360.entity.Doctor;
import com.clinic360.clinic360.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
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
} 