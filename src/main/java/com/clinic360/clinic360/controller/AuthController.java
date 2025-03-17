package com.clinic360.clinic360.controller;

import com.clinic360.clinic360.dto.PatientRegistrationRequest;
import com.clinic360.clinic360.entity.User;
import com.clinic360.clinic360.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        // Check if user is already logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && 
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("patient", new PatientRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerPatient(@ModelAttribute("patient") PatientRegistrationRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            // Validate passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }

            // Register the patient
            User patient = userService.registerPatient(request);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("DOCTOR"))) {
            return "redirect:/doctor/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("PATIENT"))) {
            return "redirect:/patient/dashboard";
        }
        
        return "redirect:/login";
    }
} 