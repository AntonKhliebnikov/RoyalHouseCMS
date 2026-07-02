package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingApartmentsForm;
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
public class AdminNewBuildingApartmentsController {

    private final AdminNewBuildingCommandService adminNewBuildingCommandService;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

    @GetMapping("/{id}/apartments")
    public String showApartmentsForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addApartmentsFormAttributes(model, newBuilding, "edit");
        return "admin/newbuildings/apartments";
    }

    @PostMapping("/{id}/apartments")
    public String updateApartments(
            @PathVariable Long id,
            @Valid @ModelAttribute("apartmentsForm") AdminNewBuildingApartmentsForm apartmentsForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (bindingResult.hasErrors()) {
                NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
                addBaseAttributes(model, newBuilding, "edit");
                return "admin/newbuildings/apartments";
            }

            adminNewBuildingCommandService.updateApartments(id, apartmentsForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Квартиры\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/apartments";
        } catch (BusinessValidationException e) {
            bindingResult.reject("apartments.validation", e.getMessage());
            NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
            addBaseAttributes(model, newBuilding, "edit");
            return "admin/newbuildings/apartments";
        }
    }

    @GetMapping("/{id}/apartments/view")
    public String viewApartments(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addApartmentsFormAttributes(model, newBuilding, "view");
        return "admin/newbuildings/apartments-view";
    }

    private void addBaseAttributes(Model model, NewBuilding newBuilding, String mode) {
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("mode", mode);
        model.addAttribute("activeTab", "apartments");
    }

    private void addApartmentsFormAttributes(Model model, NewBuilding newBuilding, String mode) {
        addBaseAttributes(model, newBuilding, mode);
        model.addAttribute("apartmentsForm", adminNewBuildingQueryService.getApartmentsForm(newBuilding));
    }
}