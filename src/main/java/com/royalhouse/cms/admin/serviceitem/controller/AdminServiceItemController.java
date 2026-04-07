package com.royalhouse.cms.admin.serviceitem.controller;

import com.royalhouse.cms.admin.serviceitem.dto.AdminServiceItemCreateOrUpdateForm;
import com.royalhouse.cms.admin.serviceitem.dto.AdminServiceItemFilterForm;
import com.royalhouse.cms.admin.serviceitem.service.AdminServiceItemService;
import com.royalhouse.cms.core.serviceitem.entity.ServiceItem;
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
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class AdminServiceItemController {
    private final AdminServiceItemService adminServiceItemService;

    @GetMapping
    public String listServices(
            @ModelAttribute("filter") AdminServiceItemFilterForm filter,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<ServiceItem> page = adminServiceItemService.findAll(filter, pageable);
        model.addAttribute("page", page);
        return "admin/services/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("form", new AdminServiceItemCreateOrUpdateForm());
        return "admin/services/new";
    }

    @PostMapping("/new")
    public String createServiceItem(
            @Valid @ModelAttribute("form") AdminServiceItemCreateOrUpdateForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/services/new";
        }

        adminServiceItemService.createServiceItem(form);
        redirectAttributes.addFlashAttribute("success", "Услуга успешно создана");
        return "redirect:/admin/services";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ServiceItem serviceItem = adminServiceItemService.getById(id);
        AdminServiceItemCreateOrUpdateForm form = new AdminServiceItemCreateOrUpdateForm();
        form.setName(serviceItem.getName());
        form.setDescription(serviceItem.getDescription());
        form.setCurrentBannerImagePath(serviceItem.getBannerImagePath());
        form.setCurrentPreviewImagePath(serviceItem.getPreviewImagePath());
        form.setIsVisible(serviceItem.getIsVisible());
        model.addAttribute("serviceItemId", id);
        model.addAttribute("serviceItemTitle", serviceItem.getName());
        model.addAttribute("form", form);
        return "admin/services/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") AdminServiceItemCreateOrUpdateForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("serviceItemId", id);
            model.addAttribute("serviceItemTitle", form.getName());
            return "admin/services/edit";
        }

        adminServiceItemService.update(id, form);
        redirectAttributes.addFlashAttribute("success", "Услуга успешно обновлена");
        return "redirect:/admin/services";
    }

    @GetMapping("/{id}")
    public String viewServiceItem(@PathVariable Long id, Model model) {
        model.addAttribute("serviceItem", adminServiceItemService.getById(id));
        return "admin/services/view";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @ModelAttribute("filter") AdminServiceItemFilterForm filter,
                         @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                         RedirectAttributes redirectAttributes
    ) {

        adminServiceItemService.delete(id);
        Long totalServiceItemAfterDelete = adminServiceItemService.countByFilters(filter);
        int requestedPage = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int lastPage = lastPageIndex(totalServiceItemAfterDelete, size);
        int safePage = Math.min(requestedPage, lastPage);
        addListParams(redirectAttributes, filter, pageable, safePage);
        redirectAttributes.addFlashAttribute("success", "Услуга удалена");
        return "redirect:/admin/services";
    }

    private void addListParams(
            RedirectAttributes redirectAttributes,
            AdminServiceItemFilterForm filter,
            Pageable pageable,
            int pageNumberOverride
    ) {
        redirectAttributes.addAttribute("page", pageNumberOverride);
        redirectAttributes.addAttribute("size", pageable.getPageSize());

        if (filter.getName() != null) {
            redirectAttributes.addAttribute("name", filter.getName());
        }

        if (filter.getIsVisible() != null) {
            redirectAttributes.addAttribute("isVisible", filter.getIsVisible());
        }
    }

    private int lastPageIndex(long totalServiceItem, int pageSize) {
        if (totalServiceItem <= 0) return 0;
        return (int) ((totalServiceItem - 1) / pageSize);
    }
}