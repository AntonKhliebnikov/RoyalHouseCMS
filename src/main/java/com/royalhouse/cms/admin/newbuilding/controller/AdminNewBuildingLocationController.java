package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingLocationForm;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingCommandService;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingQueryService;
import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/new-buildings")
@RequiredArgsConstructor
public class AdminNewBuildingLocationController {

    private final AdminNewBuildingCommandService adminNewBuildingCommandService;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

    @GetMapping("/{id}/location")
    public String showLocationForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("locationForm", adminNewBuildingQueryService.getLocationForm(newBuilding));
        model.addAttribute("mode", "edit");
        model.addAttribute("activeTab", "location");
        return "admin/newbuildings/location";
    }

    @PostMapping("/{id}/location")
    public String updateLocation(
            @PathVariable Long id,
            @Valid @ModelAttribute("locationForm") AdminNewBuildingLocationForm locationForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "location");
            return "admin/newbuildings/location";
        }

        adminNewBuildingCommandService.updateLocation(id, locationForm);
        redirectAttributes.addFlashAttribute("success", "Вкладка \"Местоположение\" обновлена");
        redirectAttributes.addAttribute("id", id);
        return "redirect:/admin/new-buildings/{id}/location";
    }

    @GetMapping("/{id}/location/view")
    public String viewLocation(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("locationForm", adminNewBuildingQueryService.getLocationForm(newBuilding));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "location");
        return "admin/newbuildings/location-view";
    }
}
