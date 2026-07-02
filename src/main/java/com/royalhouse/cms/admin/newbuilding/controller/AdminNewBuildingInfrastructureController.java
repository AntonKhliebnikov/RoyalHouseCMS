package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingInfrastructureForm;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingCommandService;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingQueryService;
import com.royalhouse.cms.core.common.exception.BusinessValidationException;
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
public class AdminNewBuildingInfrastructureController {

    private final AdminNewBuildingCommandService adminNewBuildingCommandService;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

    @GetMapping("/{id}/infrastructure")
    public String showInfrastructureForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addInfrastructureFormAttributes(model, newBuilding, "edit");
        return "admin/newbuildings/infrastructure";
    }

    @PostMapping("/{id}/infrastructure")
    public String updateInfrastructure(
            @PathVariable Long id,
            @Valid @ModelAttribute("infrastructureForm") AdminNewBuildingInfrastructureForm infrastructureForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (bindingResult.hasErrors()) {
                NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
                addBaseAttributes(model, newBuilding, "edit");
                return "admin/newbuildings/infrastructure";
            }

            adminNewBuildingCommandService.updateInfrastructure(id, infrastructureForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Инфраструктура\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/infrastructure";
        } catch (BusinessValidationException e) {
            bindingResult.reject("infrastructure.validation", e.getMessage());
            NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
            addBaseAttributes(model, newBuilding, "edit");
            return "admin/newbuildings/infrastructure";
        }
    }

    @GetMapping("/{id}/infrastructure/view")
    public String viewInfrastructure(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addInfrastructureFormAttributes(model, newBuilding, "view");
        return "admin/newbuildings/infrastructure-view";
    }

    private void addBaseAttributes(Model model, NewBuilding newBuilding, String mode) {
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("mode", mode);
        model.addAttribute("activeTab", "infrastructure");
    }

    private void addInfrastructureFormAttributes(Model model, NewBuilding newBuilding, String mode) {
        addBaseAttributes(model, newBuilding, mode);
        model.addAttribute("infrastructureForm", adminNewBuildingQueryService.getInfrastructureForm(newBuilding));
    }
}
