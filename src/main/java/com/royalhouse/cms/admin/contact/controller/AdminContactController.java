package com.royalhouse.cms.admin.contact.controller;

import com.royalhouse.cms.admin.contact.dto.AdminContactSettingsForm;
import com.royalhouse.cms.admin.contact.dto.AdminRecipientEmailForm;
import com.royalhouse.cms.admin.contact.service.AdminContactService;
import com.royalhouse.cms.core.application.exception.ApplicationRecipientEmailNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings/contacts")
@RequiredArgsConstructor
public class AdminContactController {
    private final AdminContactService adminContactService;

    @GetMapping
    public String showContactPage(Model model) {
        fillPageModel(model);
        return "admin/settings/contacts";
    }

    @PostMapping
    public String updateContactSettings(
            @Valid @ModelAttribute("contactForm") AdminContactSettingsForm contactForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            fillPageModel(model);
            return "admin/settings/contacts";
        }

        adminContactService.updateSettings(contactForm);
        redirectAttributes.addFlashAttribute("success", "Контакты успешно обновлены");
        return "redirect:/admin/settings/contacts";
    }

    @PostMapping("/recipient-emails")
    public String addRecipientEmail(
            @Valid @ModelAttribute("recipientEmailForm") AdminRecipientEmailForm recipientEmailForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            fillPageModel(model);
            return "admin/settings/contacts";
        }

        try {
            adminContactService.addRecipientEmail(recipientEmailForm);
            redirectAttributes.addFlashAttribute("success", "mail получателя успешно добавлен");
            return "redirect:/admin/settings/contacts";
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("email", "recipientEmailForm.email", e.getMessage());
            fillPageModel(model);
            return "admin/settings/contacts";
        }
    }

    @PostMapping("/recipient-emails/{id}/disable")
    public String disableRecipientEmail(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminContactService.disableRecipientEmail(id);
            redirectAttributes.addFlashAttribute("success", "Email получателя отключен");
        } catch (ApplicationRecipientEmailNotFoundException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/settings/contacts";
    }

    @PostMapping("/recipient-emails/{id}/enable")
    public String enableRecipientEmail(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminContactService.enableRecipientEmail(id);
            redirectAttributes.addFlashAttribute("success", "Email получателя включен");
        } catch (ApplicationRecipientEmailNotFoundException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/settings/contacts";
    }

    @PostMapping("/recipient-emails/{id}/delete")
    public String deleteRecipientEmail(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminContactService.deleteRecipientEmail(id);
            redirectAttributes.addFlashAttribute("success", "Email получателя удален");
        } catch (ApplicationRecipientEmailNotFoundException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/settings/contacts";
    }

    private void fillPageModel(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", adminContactService.getSettingsForm());
        }

        if (!model.containsAttribute("recipientEmailForm")) {
            model.addAttribute("recipientEmailForm", new AdminRecipientEmailForm());
        }

        model.addAttribute("recipientEmails", adminContactService.getAllRecipientEmails());
    }
}
