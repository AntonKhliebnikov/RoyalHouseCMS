package com.royalhouse.cms.admin.settings.contact.controller;

import com.royalhouse.cms.admin.settings.contact.dto.AdminContactsPageForm;
import com.royalhouse.cms.admin.settings.contact.service.AdminContactService;
import com.royalhouse.cms.core.application.exception.ApplicationRecipientEmailNotFoundException;
import com.royalhouse.cms.core.contact.exception.ContactSettingsNotFoundException;
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
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", adminContactService.getContactsPageForm());
        }
        return "admin/settings/contacts";
    }

    @PostMapping
    public String saveContactPage(
            @Valid @ModelAttribute("form") AdminContactsPageForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/settings/contacts";
        }

        try {
            adminContactService.saveContactsPage(form);
        } catch (ContactSettingsNotFoundException
                 | ApplicationRecipientEmailNotFoundException
                 | IllegalStateException e) {
            bindingResult.reject("contacts.save", e.getMessage());
            return "admin/settings/contacts";
        }

        redirectAttributes.addFlashAttribute("success", "Контакты успешно обновлены");
        return "redirect:/admin/settings/contacts";
    }
}
