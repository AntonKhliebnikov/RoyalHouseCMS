package com.royalhouse.cms.admin.settings.secondarymarket.controller;

import com.royalhouse.cms.admin.settings.secondarymarket.dto.SecondaryMarketSettingsForm;
import com.royalhouse.cms.admin.settings.secondarymarket.service.SecondaryMarketSlideService;
import com.royalhouse.cms.core.secondarymarket.exception.SecondaryMarketSlideException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/settings/secondary-market")
public class AdminSecondaryMarketController {
    private final SecondaryMarketSlideService slideService;

    @GetMapping
    public String showSecondaryMarketPage(Model model) {
        model.addAttribute("form", slideService.getSettingsForm());
        return "admin/settings/secondary-market/slides";
    }

    @PostMapping
    public String saveSecondaryMarketPage(
            @ModelAttribute("form") SecondaryMarketSettingsForm form,
            RedirectAttributes redirectAttributes
    ) {
        try {
            slideService.saveSettings(form);
            redirectAttributes.addFlashAttribute("success",
                    "Настройки страницы «Вторичный рынок» сохранены");
        } catch (SecondaryMarketSlideException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/settings/secondary-market";
    }
}