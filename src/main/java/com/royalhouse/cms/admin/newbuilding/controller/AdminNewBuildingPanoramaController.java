package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingPanoramaForm;
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
public class AdminNewBuildingPanoramaController {

    private final AdminNewBuildingCommandService adminNewBuildingCommandService;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

    @GetMapping("/{id}/panorama")
    public String showPanoramaForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("panoramaForm", adminNewBuildingQueryService.getPanoramaForm(newBuilding));
        model.addAttribute("mode", "edit");
        model.addAttribute("activeTab", "panorama");
        return "admin/newbuildings/panorama";
    }

    @PostMapping("/{id}/panorama")
    public String updatePanorama(
            @PathVariable Long id,
            @Valid @ModelAttribute("panoramaForm") AdminNewBuildingPanoramaForm panoramaForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "panorama");
                return "admin/newbuildings/panorama";
            }

            adminNewBuildingCommandService.updatePanorama(id, panoramaForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Панорама\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/panorama";
        } catch (BusinessValidationException e) {
            bindingResult.reject("panorama.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "panorama");
            return "admin/newbuildings/panorama";
        }
    }

    @GetMapping("/{id}/panorama/view")
    public String viewPanorama(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("panoramaForm", adminNewBuildingQueryService.getPanoramaForm(newBuilding));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "panorama");
        return "admin/newbuildings/panorama-view";
    }
}
