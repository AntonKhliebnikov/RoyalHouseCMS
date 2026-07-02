package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.common.util.AdminPaginationUtils;
import com.royalhouse.cms.admin.newbuilding.dto.*;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingCommandService;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingQueryService;
import com.royalhouse.cms.core.common.exception.BusinessValidationException;
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
    private final AdminNewBuildingCommandService adminNewBuildingCommandService;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

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
        Page<NewBuilding> page = adminNewBuildingQueryService.findAll(filterForm, pageable);
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

        Long newBuildingId = adminNewBuildingCommandService.createNewBuilding(createForm);
        redirectAttributes.addFlashAttribute("success", "Новострой успешно создан");
        redirectAttributes.addAttribute("id", newBuildingId);
        return "redirect:/admin/new-buildings/{id}/edit";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addBasicFormAttributes(model, newBuilding, "edit");
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
        try {
            if (bindingResult.hasErrors()) {
                NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
                addBaseAttributes(model, newBuilding, "edit");
                return "admin/newbuildings/edit";
            }

            adminNewBuildingCommandService.updateBasic(id, basicForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Основное\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/edit";
        } catch (BusinessValidationException e) {
            bindingResult.reject("basic.validation", e.getMessage());
            NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
            addBaseAttributes(model, newBuilding, "edit");
            return "admin/newbuildings/edit";
        }
    }

    @GetMapping("/{id}")
    public String viewNewBuilding(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        addBasicFormAttributes(model, newBuilding, "view");
        return "admin/newbuildings/view";
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            @ModelAttribute("filter") AdminNewBuildingFilterForm filter,
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "sortOrder", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @PageableDefault(size = 5) Pageable pageable,
            RedirectAttributes redirectAttributes
    ) {
        adminNewBuildingCommandService.delete(id);
        Long totalNewBuildingsAfterDelete = adminNewBuildingQueryService.countByFilters(filter);
        int requestedPage = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int lastPage = AdminPaginationUtils.lastPageIndex(totalNewBuildingsAfterDelete, size);
        int safePage = Math.min(requestedPage, lastPage);
        addListParams(redirectAttributes, filter, pageable, safePage);
        redirectAttributes.addFlashAttribute("success", "Новострой удален");
        return "redirect:/admin/new-buildings";
    }

    private void addListParams(
            RedirectAttributes redirectAttributes,
            AdminNewBuildingFilterForm filter,
            Pageable pageable,
            int pageNumberOverride
    ) {
        redirectAttributes.addAttribute("page", pageNumberOverride);
        redirectAttributes.addAttribute("size", pageable.getPageSize());

        if (filter.getName() != null) {
            redirectAttributes.addAttribute("name", filter.getName());
        }

        if (filter.getAddress() != null) {
            redirectAttributes.addAttribute("address", filter.getAddress());
        }

        if (filter.getIsActive() != null) {
            redirectAttributes.addAttribute("isActive", filter.getIsActive());
        }
    }

    private void addBaseAttributes(Model model, NewBuilding newBuilding, String mode) {
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("mode", mode);
        model.addAttribute("activeTab", "basic");
    }

    private void addBasicFormAttributes(Model model, NewBuilding newBuilding, String mode) {
        addBaseAttributes(model, newBuilding, mode);
        model.addAttribute("basicForm", adminNewBuildingQueryService.getBasicForm(newBuilding));
    }
}