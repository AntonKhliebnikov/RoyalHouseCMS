package com.royalhouse.cms.admin.settings.propertybinding.controller;

import com.royalhouse.cms.admin.settings.propertybinding.dto.AttachPropertyForm;
import com.royalhouse.cms.admin.settings.propertybinding.service.NewBuildingPropertyBindingService;
import com.royalhouse.cms.core.propertybinding.exception.PropertyBindingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/settings/property-bindings")
public class AdminNewBuildingPropertyBindingController {
    private final NewBuildingPropertyBindingService bindingService;

    @GetMapping
    public String listNewBuildings(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 5, sort = "sortOrder", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {
        model.addAttribute("page", bindingService.getNewBuildingRows(name, pageable));
        model.addAttribute("name", name);
        return "admin/settings/property-bindings/list";
    }

    @GetMapping("/{newBuildingId}")
    public String manageBindings(@PathVariable Long newBuildingId, Model model) {
        model.addAttribute("page", bindingService.getManagePage(newBuildingId));
        model.addAttribute("attachForm", new AttachPropertyForm());
        return "admin/settings/property-bindings/manage";
    }

    @PostMapping("/{newBuildingId}/attach")
    public String attachProperties(
            @PathVariable Long newBuildingId,
            @ModelAttribute("attachForm") AttachPropertyForm attachForm,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bindingService.attachProperties(newBuildingId, attachForm.getPropertyIds());
            redirectAttributes.addFlashAttribute("success", "Объекты недвижимости привязаны");
        } catch (PropertyBindingException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/settings/property-bindings/" + newBuildingId;
    }

    @PostMapping("/{newBuildingId}/attach/{propertyId}")
    public String attachSingleProperty(
            @PathVariable Long newBuildingId,
            @PathVariable Long propertyId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bindingService.attachProperty(newBuildingId, propertyId);
            redirectAttributes.addFlashAttribute("success", "Объект недвижимости привязан");
        } catch (PropertyBindingException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/settings/property-bindings/" + newBuildingId;
    }

    @PostMapping("/{newBuildingId}/detach/{propertyId}")
    public String detachProperty(
            @PathVariable Long newBuildingId,
            @PathVariable Long propertyId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bindingService.detachProperty(newBuildingId, propertyId);
            redirectAttributes.addFlashAttribute("success", "Объект недвижимости отвязан");
        } catch (PropertyBindingException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/settings/property-bindings/" + newBuildingId;
    }

    @PostMapping("/{newBuildingId}/attach-address-candidates")
    public String attachAddressCandidates(
            @PathVariable Long newBuildingId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bindingService.attachAllCandidatesByAddress(newBuildingId);
            redirectAttributes.addFlashAttribute("success", "Кандидаты по адресу привязаны");
        } catch (PropertyBindingException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/settings/property-bindings/" + newBuildingId;
    }
}