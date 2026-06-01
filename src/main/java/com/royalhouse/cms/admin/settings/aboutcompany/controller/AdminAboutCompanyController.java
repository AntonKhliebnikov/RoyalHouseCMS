package com.royalhouse.cms.admin.settings.aboutcompany.controller;

import com.royalhouse.cms.admin.settings.aboutcompany.dto.AboutCompanySettingsForm;
import com.royalhouse.cms.admin.settings.aboutcompany.service.AboutCompanySettingsService;
import com.royalhouse.cms.core.aboutcompany.exception.AboutCompanySettingsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings/about-company")
@RequiredArgsConstructor
public class AdminAboutCompanyController {
    private final AboutCompanySettingsService aboutCompanySettingsService;

    @GetMapping
    public String showAboutCompanyPage(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", aboutCompanySettingsService.getSettingsForm());
        }

        return "admin/settings/about-company/info";
    }

    @PostMapping
    public String updateAboutCompanyPage(
            @Valid @ModelAttribute("form") AboutCompanySettingsForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/settings/about-company/info";
        }

        try {
            aboutCompanySettingsService.saveSettings(form);
            redirectAttributes.addFlashAttribute("success", "Страница \"О компании\" успешно сохранена");
            return "redirect:/admin/settings/about-company";
        } catch (AboutCompanySettingsException e) {
            bindingResult.reject("error", e.getMessage());
            return "admin/settings/about-company/info";
        }
    }
}