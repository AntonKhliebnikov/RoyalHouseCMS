package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingSpecificationForm;
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
public class AdminNewBuildingSpecificationController {

    private final AdminNewBuildingCommandService adminNewBuildingCommandService;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

    @GetMapping("/{id}/specification")
    public String showSpecificationForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addSpecificationFormAttributes(model, newBuilding, "edit");
        return "admin/newbuildings/specification";
    }

    @PostMapping("/{id}/specification")
    public String updateSpecification(
            @PathVariable Long id,
            @Valid @ModelAttribute("specificationForm") AdminNewBuildingSpecificationForm specificationForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (bindingResult.hasErrors()) {
                NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
                addBaseAttributes(model, newBuilding, "edit");
                return "admin/newbuildings/specification";
            }

            adminNewBuildingCommandService.updateSpecification(id, specificationForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Спецификация\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/specification";
        } catch (BusinessValidationException e) {
            bindingResult.reject("specification.validation", e.getMessage());
            NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
            addBaseAttributes(model, newBuilding, "edit");
            return "admin/newbuildings/specification";
        }
    }

    @GetMapping("/{id}/specification/view")
    public String viewSpecification(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addSpecificationFormAttributes(model, newBuilding, "view");
        return "admin/newbuildings/specification-view";
    }

    private void addBaseAttributes(Model model, NewBuilding newBuilding, String mode) {
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("mode", mode);
        model.addAttribute("activeTab", "specification");
    }

    private void addSpecificationFormAttributes(Model model, NewBuilding newBuilding, String mode) {
        addBaseAttributes(model, newBuilding, mode);
        model.addAttribute("specificationForm", adminNewBuildingQueryService.getSpecificationForm(newBuilding));
    }
}
