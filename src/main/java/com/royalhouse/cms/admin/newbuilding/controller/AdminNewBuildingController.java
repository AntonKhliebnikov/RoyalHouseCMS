package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingBasicForm;
import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingCreateForm;
import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingFilterForm;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingService;
import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/new-buildings")
@RequiredArgsConstructor
public class AdminNewBuildingController {
    private final AdminNewBuildingService adminNewBuildingService;

    @GetMapping
    public String listNewBuildings(
            @ModelAttribute("filter") AdminNewBuildingFilterForm filterForm,
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "sortOrder", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @PageableDefault(size = 5) Pageable pageable,
            Model model
    ) {
        Page<NewBuilding> page = adminNewBuildingService.findAll(filterForm, pageable);
        model.addAttribute("page", page);
        return "admin/newbuildings/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createForm", new AdminNewBuildingCreateForm());
        model.addAttribute("mode", "create");
        model.addAttribute("activeTab", "basic");
        return "admin/newbuildings/new";
    }

    @PostMapping
    public String createNewBuilding(
            @Valid @ModelAttribute("createForm") AdminNewBuildingCreateForm createForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            model.addAttribute("activeTab", "basic");
            return "admin/newbuildings/new";
        }

        Long newBuildingId = adminNewBuildingService.createInitial(createForm);

        redirectAttributes.addFlashAttribute("success", "Новострой успешно создан");
        redirectAttributes.addAttribute("id", newBuildingId);
        return "redirect:/admin/new-buildings/{id}/edit";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("basicForm", adminNewBuildingService.getBasicFormById(id));
        model.addAttribute("mode", "edit");
        model.addAttribute("activeTab", "basic");
        return "admin/newbuildings/edit";
    }

    @PostMapping("/{id}/basic")
    public String updateBasic(
            @PathVariable Long id,
            @Valid @ModelAttribute("basicForm") AdminNewBuildingBasicForm basicForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "basic");
            return "admin/newbuildings/edit";
        }

        try {
            adminNewBuildingService.updateBasic(id, basicForm);
            redirectAttributes.addFlashAttribute("success", "Основная информация обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/edit";
        } catch (IllegalArgumentException e) {
            model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "basic");
            model.addAttribute("error", e.getMessage());
            return "admin/newbuildings/edit";
        }
    }

    @GetMapping("/{id}")
    public String viewNewBuilding(@PathVariable Long id, Model model) {
        model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
        model.addAttribute("basicForm", adminNewBuildingService.getBasicFormById(id));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "basic");
        return "admin/newbuildings/view";
    }
}