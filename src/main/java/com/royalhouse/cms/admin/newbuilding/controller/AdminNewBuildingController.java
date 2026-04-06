package com.royalhouse.cms.admin.newbuilding.controller;

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
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("basicForm", adminNewBuildingQueryService.getBasicForm(newBuilding));
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
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "basic");
                return "admin/newbuildings/edit";
            }

            adminNewBuildingCommandService.updateBasic(id, basicForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Основное\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/edit";
        } catch (BusinessValidationException e) {
            bindingResult.reject("basic.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "basic");
            return "admin/newbuildings/edit";
        }
    }

    @GetMapping("/{id}")
    public String viewNewBuilding(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("basicForm", adminNewBuildingQueryService.getBasicForm(newBuilding));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "basic");
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
        int lastPage = lastPageIndex(totalNewBuildingsAfterDelete, size);
        int safePage = Math.min(requestedPage, lastPage);
        addListParams(redirectAttributes, filter, pageable, safePage);
        redirectAttributes.addFlashAttribute("success", "Новострой удален");
        return "redirect:/admin/new-buildings";
    }

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

    @GetMapping("/{id}/infrastructure")
    public String showInfrastructureForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("infrastructureForm", adminNewBuildingQueryService.getInfrastructureForm(newBuilding));
        model.addAttribute("mode", "edit");
        model.addAttribute("activeTab", "infrastructure");
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
                model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "infrastructure");
                return "admin/newbuildings/infrastructure";
            }

            adminNewBuildingCommandService.updateInfrastructure(id, infrastructureForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Инфраструктура\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/infrastructure";
        } catch (BusinessValidationException e) {
            bindingResult.reject("infrastructure.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "infrastructure");
            return "admin/newbuildings/infrastructure";
        }
    }

    @GetMapping("/{id}/infrastructure/view")
    public String viewInfrastructure(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("infrastructureForm", adminNewBuildingQueryService.getInfrastructureForm(newBuilding));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "infrastructure");
        return "admin/newbuildings/infrastructure-view";
    }

    @GetMapping("/{id}/apartments")
    public String showApartmentsForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("apartmentsForm", adminNewBuildingQueryService.getApartmentsForm(newBuilding));
        model.addAttribute("mode", "edit");
        model.addAttribute("activeTab", "apartments");
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
                model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "apartments");
                return "admin/newbuildings/apartments";
            }

            adminNewBuildingCommandService.updateApartments(id, apartmentsForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Квартиры\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/apartments";
        } catch (BusinessValidationException e) {
            bindingResult.reject("apartments.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "apartments");
            return "admin/newbuildings/apartments";
        }
    }

    @GetMapping("/{id}/apartments/view")
    public String viewApartments(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("apartmentsForm", adminNewBuildingQueryService.getApartmentsForm(newBuilding));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "apartments");
        return "admin/newbuildings/apartments-view";
    }

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

    @GetMapping("/{id}/specification")
    public String showSpecificationForm(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("specificationForm", adminNewBuildingQueryService.getSpecificationForm(newBuilding));
        model.addAttribute("mode", "edit");
        model.addAttribute("activeTab", "specification");
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
                model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
                model.addAttribute("mode", "edit");
                model.addAttribute("activeTab", "specification");
                return "admin/newbuildings/specification";
            }

            adminNewBuildingCommandService.updateSpecification(id, specificationForm);
            redirectAttributes.addFlashAttribute("success", "Вкладка \"Спецификация\" обновлена");
            redirectAttributes.addAttribute("id", id);
            return "redirect:/admin/new-buildings/{id}/specification";
        } catch (BusinessValidationException e) {
            bindingResult.reject("specification.validation", e.getMessage());
            model.addAttribute("newBuilding", adminNewBuildingQueryService.getById(id));
            model.addAttribute("mode", "edit");
            model.addAttribute("activeTab", "specification");
            return "admin/newbuildings/specification";
        }
    }

    @GetMapping("/{id}/specification/view")
    public String viewSpecification(@PathVariable Long id, Model model) {
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        model.addAttribute("newBuilding", newBuilding);
        model.addAttribute("specificationForm", adminNewBuildingQueryService.getSpecificationForm(newBuilding));
        model.addAttribute("mode", "view");
        model.addAttribute("activeTab", "specification");
        return "admin/newbuildings/specification-view";
    }

    private int lastPageIndex(long totalNewBuildings, int pageSize) {
        if (totalNewBuildings <= 0) return 0;
        return (int) ((totalNewBuildings - 1) / pageSize);
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