package com.royalhouse.cms.admin.property.controller;

import com.royalhouse.cms.admin.property.dto.AdminPropertyCreateOrUpdateForm;
import com.royalhouse.cms.admin.property.dto.PropertyFilterForm;
import com.royalhouse.cms.admin.property.service.PropertyService;
import com.royalhouse.cms.core.property.entity.Property;
import com.royalhouse.cms.core.property.entity.PropertyType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/properties")
public class AdminPropertyController {
    private final PropertyService propertyService;

    @GetMapping
    public String listProperties(
            @Valid @ModelAttribute("filter") PropertyFilterForm filter,
            BindingResult bindingResult,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<Property> page;

        if (bindingResult.hasErrors()) {
            page = Page.empty(pageable);
        } else {
            page = propertyService.findAll(filter, pageable);
        }

        model.addAttribute("page", page);
        return "admin/properties/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new AdminPropertyCreateOrUpdateForm());
        return "admin/properties/new";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") AdminPropertyCreateOrUpdateForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/properties/new";
        }

        propertyService.create(form);
        redirectAttributes.addFlashAttribute("success", "New property created");
        return "redirect:/admin/properties";
    }

    @ModelAttribute("propertyTypeOptions")
    public PropertyType[] propertyTypeOptions() {
        return PropertyType.values();
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Property property = propertyService.getById(id);
            AdminPropertyCreateOrUpdateForm form = new AdminPropertyCreateOrUpdateForm();
            form.setPropertyType(property.getPropertyType());
            form.setArea(property.getArea());
            form.setPrice(property.getPrice());
            form.setRooms(property.getRooms());
            form.setFloor(property.getFloor());
            form.setTotalFloors(property.getTotalFloors());
            model.addAttribute("propertyId", id);
            model.addAttribute("form", form);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/properties";
        }
        return "admin/properties/edit";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") AdminPropertyCreateOrUpdateForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyId", id);
            return "admin/properties/edit";
        }

        try {
            propertyService.update(id, form);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("propertyId", id);
            return "admin/properties/edit";
        }

        redirectAttributes.addFlashAttribute("success", "Property updated");
        return "redirect:/admin/properties";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @ModelAttribute("filter") PropertyFilterForm filter,
                         @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                         RedirectAttributes redirectAttributes
    ) {
        try {
            propertyService.delete(id);
            long totalPropertiesAfterDelete = propertyService.countByFilters(filter);
            int requestedPage = pageable.getPageNumber();
            int size = pageable.getPageSize();
            int lastPage = lastPageIndex(totalPropertiesAfterDelete, size);
            int safePage = Math.min(requestedPage, lastPage);
            addListParams(redirectAttributes, filter, pageable, safePage);

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/properties";
        }

        redirectAttributes.addFlashAttribute("success", "Объект удален");
        return "redirect:/admin/properties";
    }

    @GetMapping("/{id}")
    public String view(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            model.addAttribute("property", propertyService.getById(id));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/properties";
        }
        return "admin/properties/view";
    }

    private void addListParams(
            RedirectAttributes redirectAttributes,
            PropertyFilterForm filter,
            Pageable pageable,
            int pageNumberOverride
    ) {
        redirectAttributes.addAttribute("page", pageNumberOverride);
        redirectAttributes.addAttribute("size", pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                redirectAttributes.addAttribute(
                        "sort",
                        order.getProperty() + "," + order.getDirection().name().toLowerCase()
                );
            }
        }

        if (filter.getId() != null)
            redirectAttributes.addAttribute("id", filter.getId());

        if (filter.getPropertyType() != null)
            redirectAttributes.addAttribute("propertyType", filter.getPropertyType());

        if (filter.getAreaFrom() != null)
            redirectAttributes.addAttribute("areaFrom", filter.getAreaFrom());

        if (filter.getAreaTo() != null)
            redirectAttributes.addAttribute("areaTo", filter.getAreaTo());

        if (filter.getPriceFrom() != null)
            redirectAttributes.addAttribute("priceFrom", filter.getPriceFrom());

        if (filter.getPriceTo() != null)
            redirectAttributes.addAttribute("priceTo", filter.getPriceTo());

        if (filter.getRooms() != null)
            redirectAttributes.addAttribute("rooms", filter.getRooms());
    }

    private int lastPageIndex(long totalProperties, int pageSize) {
        if (totalProperties <= 0) return 0;
        return (int) ((totalProperties - 1) / pageSize);
    }
}