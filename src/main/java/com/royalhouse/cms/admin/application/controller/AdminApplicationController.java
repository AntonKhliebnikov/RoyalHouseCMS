package com.royalhouse.cms.admin.application.controller;

import com.royalhouse.cms.admin.application.dto.ApplicationFilterForm;
import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import com.royalhouse.cms.core.application.service.ApplicationService;
import com.royalhouse.cms.core.application.service.ApplicationXlsxExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/applications")
public class AdminApplicationController {
    private final ApplicationService applicationService;
    private final ApplicationXlsxExportService applicationXlsxExportService;

    @GetMapping
    public String list(
            @ModelAttribute("filter") ApplicationFilterForm filter,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<Application> page = applicationService.search(
                filter.getFullName(),
                filter.getPhone(),
                filter.getEmail(),
                filter.getComment(),
                filter.getStatus(),
                pageable
        );

        model.addAttribute("page", page);
        model.addAttribute("statusOptions", ApplicationStatus.values());
        return "admin/applications/list";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(
            @PathVariable long id,
            @ModelAttribute("filter") ApplicationFilterForm filter,
            Pageable pageable,
            RedirectAttributes redirectAttributes
    ) {
        applicationService.toggleStatus(id);
        addListParams(redirectAttributes, filter, pageable, pageable.getPageNumber());
        return "redirect:/admin/applications";
    }

    @PostMapping("/{id}/toggle-status-view")
    public String toggleStatusFromView(@PathVariable long id) {
        applicationService.toggleStatus(id);
        return "redirect:/admin/applications/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable long id,
            @ModelAttribute("filter") ApplicationFilterForm filter,
            Pageable pageable,
            RedirectAttributes redirectAttributes
    ) {
        applicationService.delete(id);
        long totalAppAfterDelete = applicationService.countByFilters(
                filter.getFullName(),
                filter.getPhone(),
                filter.getEmail(),
                filter.getComment(),
                filter.getStatus()
        );

        int requestedPage = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int lastPage = lastPageIndex(totalAppAfterDelete, size);
        int safePage = Math.min(requestedPage, lastPage);
        addListParams(redirectAttributes, filter, pageable, safePage);
        return "redirect:/admin/applications";
    }

    @GetMapping("/{id}")
    public String view(
            @PathVariable long id,
            Model model) {
        model.addAttribute("app", applicationService.getById(id));
        return "admin/applications/view";
    }

    @GetMapping("/export")
    public void exportXlsx(
            @ModelAttribute("filter") ApplicationFilterForm filter,
            HttpServletResponse response
    ) throws IOException {
        byte[] bytes = applicationXlsxExportService.exportXlsx(
                filter.getFullName(),
                filter.getPhone(),
                filter.getEmail(),
                filter.getComment(),
                filter.getStatus(),
                ZoneId.of("Europe/Kyiv")
        );

        String fileName = "application.xlsx";
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        response.setContentLength(bytes.length);

        response.getOutputStream().write(bytes);
        response.flushBuffer();
    }

    private void addListParams(
            RedirectAttributes redirectAttributes,
            ApplicationFilterForm filter,
            Pageable pageable,
            int pageNumberOverride
    ) {
        redirectAttributes.addAttribute("page", pageNumberOverride);
        redirectAttributes.addAttribute("size", pageable.getPageSize());

        pageable.getSort();
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                redirectAttributes.addAttribute(
                        "sort",
                        order.getProperty() + "," + order.getDirection().name().toLowerCase()
                );
            }
        }

        if (filter.getFullName() != null && !filter.getFullName().isBlank())
            redirectAttributes.addAttribute("fullName", filter.getFullName());

        if (filter.getPhone() != null && !filter.getPhone().isBlank())
            redirectAttributes.addAttribute("phone", filter.getPhone());

        if (filter.getEmail() != null && !filter.getEmail().isBlank())
            redirectAttributes.addAttribute("email", filter.getEmail());

        if (filter.getComment() != null && !filter.getComment().isBlank())
            redirectAttributes.addAttribute("comment", filter.getComment());

        if (filter.getStatus() != null)
            redirectAttributes.addAttribute("status", filter.getStatus());
    }

    private int lastPageIndex(long totalApplications, int pageSize) {
        if (totalApplications <= 0) return 0;
        return (int) ((totalApplications - 1) / pageSize);
    }
}