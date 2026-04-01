package com.royalhouse.cms.admin.newbuilding.controller;

import com.royalhouse.cms.admin.newbuilding.dto.*;
import com.royalhouse.cms.admin.newbuilding.service.AdminNewBuildingService;
import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import jakarta.persistence.EntityNotFoundException;
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

        Long newBuildingId = adminNewBuildingService.createNewBuilding(createForm);
        redirectAttributes.addFlashAttribute("success", "Новострой успешно создан");
        redirectAttributes.addAttribute("id", newBuildingId);
        return "redirect:/admin/new-buildings/{id}/edit";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("basicForm", adminNewBuildingService.getBasicFormById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "basic");
            return "admin/newbuildings/edit";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
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
                model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "basic");
                return "admin/newbuildings/edit";
            }

            adminNewBuildingService.updateBasic(id, basicForm);
            redirectAttributes.addFlashAttribute("success", "Основная информация обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/edit";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/newbuildings/edit";
        }
    }

    @GetMapping("/{id}")
    public String viewNewBuilding(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
            model.addAttribute("basicForm", adminNewBuildingService.getBasicFormById(id));
            model.addAttribute("mode", "view");
            model.addAttribute("activeTab", "basic");
            return "admin/newbuildings/view";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
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
        try {
            adminNewBuildingService.delete(id);
            Long totalNewBuildingsAfterDelete = adminNewBuildingService.countByFilters(filter);
            int requestedPage = pageable.getPageNumber();
            int size = pageable.getPageSize();
            int lastPage = lastPageIndex(totalNewBuildingsAfterDelete, size);
            int safePage = Math.min(requestedPage, lastPage);
            addListParams(redirectAttributes, filter, pageable, safePage);
            redirectAttributes.addFlashAttribute("success", "Новострой удален");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }

        return "redirect:/admin/new-buildings";
    }

    @GetMapping("/{id}/about")
    public String showAboutForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("aboutForm", adminNewBuildingService.getAboutFormById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "about");
            return "admin/newbuildings/about";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
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
                model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "about");
                return "admin/newbuildings/about";
            }

            adminNewBuildingService.updateAbout(id, aboutForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка «О проекте» обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/about";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @GetMapping("/{id}/about/view")
    public String viewAbout(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("aboutForm", adminNewBuildingService.getAboutFormById(id));
            model.addAttribute("mode", "view");
            model.addAttribute("activeTab", "about");
            return "admin/newbuildings/about-view";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @GetMapping("/{id}/location")
    public String showLocationForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("locationForm", adminNewBuildingService.getLocationFormById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "location");
            return "admin/newbuildings/location";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @PostMapping("/{id}/location")
    public String updateLocation(
            @PathVariable Long id,
            @Valid @ModelAttribute("locationForm") AdminNewBuildingLocationForm locationForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "location");
                return "admin/newbuildings/location";
            }

            adminNewBuildingService.updateLocation(id, locationForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка «Местоположение» обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/location";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @GetMapping("/{id}/location/view")
    public String viewLocation(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);

            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("locationForm", adminNewBuildingService.getLocationFormById(id));
            model.addAttribute("mode", "view");
            model.addAttribute("activeTab", "location");

            return "admin/newbuildings/location-view";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @GetMapping("/{id}/infrastructure")
    public String showInfrastructureForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("infrastructureForm", adminNewBuildingService.getInfrastructureFormById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "infrastructure");
            return "admin/newbuildings/infrastructure";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
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
                model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "infrastructure");
                return "admin/newbuildings/infrastructure";
            }

            adminNewBuildingService.updateInfrastructure(id, infrastructureForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка «Инфраструктура» обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/infrastructure";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("infrastructure.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "infrastructure");
            return "admin/newbuildings/infrastructure";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @GetMapping("/{id}/infrastructure/view")
    public String viewInfrastructure(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("infrastructureForm", adminNewBuildingService.getInfrastructureFormById(id));
            model.addAttribute("mode", "view");
            model.addAttribute("activeTab", "infrastructure");
            return "admin/newbuildings/infrastructure-view";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @GetMapping("/{id}/apartments")
    public String showApartmentsForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("apartmentsForm", adminNewBuildingService.getApartmentFormById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "apartments");
            return "admin/newbuildings/apartments";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
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
                model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "apartments");
                return "admin/newbuildings/apartments";
            }

            adminNewBuildingService.updateApartments(id, apartmentsForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка «Квартиры» обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/apartments";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("apartments.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "apartments");
            return "admin/newbuildings/apartments";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    @GetMapping("/{id}/apartments/view")
    public String viewApartments(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NewBuilding newBuilding = adminNewBuildingService.getById(id);
            model.addAttribute("newBuilding", newBuilding);
            model.addAttribute("apartmentsForm", adminNewBuildingService.getApartmentFormById(id));
            model.addAttribute("mode", "view");
            model.addAttribute("activeTab", "apartments");
            return "admin/newbuildings/apartments-view";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/new-buildings";
        }
    }

    private int lastPageIndex(long totalProperties, int pageSize) {
        if (totalProperties <= 0) return 0;
        return (int) ((totalProperties - 1) / pageSize);
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
}