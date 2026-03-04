package com.royalhouse.cms.auth.controller;

import com.royalhouse.cms.core.user.repository.UserRepository;
import com.royalhouse.cms.auth.service.RegistrationService;
import com.royalhouse.cms.auth.dto.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final RegistrationService registrationService;

    public AuthController(UserRepository userRepository, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        boolean canRegister = userRepository.count() == 0;
        model.addAttribute("canRegister", canRegister);
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        if (userRepository.count() > 0) {
            return "redirect:/login";
        }
        model.addAttribute("form", new RegistrationForm());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("form") RegistrationForm form,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Passwords do not match");
            return "auth/registration";
        }

        try {
            registrationService.registrationFirstAdmin(form);
        } catch (IllegalStateException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "auth/registration";
        }

        return "redirect:/login?registered";
    }
}