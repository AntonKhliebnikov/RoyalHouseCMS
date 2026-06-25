package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingAboutForm;
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
public class AdminNewBuildingAboutController {

    private final AdminNewBuildingCommandService adminNewBuildingCommandService;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

    @GetMapping("/{id}/about")
    public String showAboutForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("aboutForm", adminNewBuildingQueryService.getAboutForm(newBuilding));
        model.addAttribute("mode", "edit");
        model.addAttribute("activeTab", "about");
        return "admin/newbuildings/about";
    }

    @PostMapping("/{id}/about")
    public String updateAbout(
            @PathVariable Long id,
            @Valid @ModelAttribute("aboutForm") AdminNewBuildingAboutForm aboutForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "about");
                return "admin/newbuildings/about";
            }

            adminNewBuildingCommandService.updateAbout(id, aboutForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"О проекте\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/about";
        } catch (BusinessValidationException e) {
            bindingResult.reject("about.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "about");
            return "admin/newbuildings/about";
        }
    }

    @GetMapping("/{id}/about/view")
    public String viewAbout(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("aboutForm", adminNewBuildingQueryService.getAboutForm(newBuilding));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "about");
        return "admin/newbuildings/about-view";
    }
}
