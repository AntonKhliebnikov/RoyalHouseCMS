package com.royalhouse.cms.admin.user.controller;

import com.royalhouse.cms.admin.user.dto.AdminUserCreateForm;
import com.royalhouse.cms.admin.user.dto.AdminUserUpdateForm;
import com.royalhouse.cms.admin.user.dto.ResetPasswordForm;
import com.royalhouse.cms.admin.user.service.AdminUserService;
import com.royalhouse.cms.core.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", adminUserService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new AdminUserCreateForm());
        return "admin/users/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") AdminUserCreateForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/users/new";
        }

        try {
            adminUserService.createAdmin(form);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "/admin/users/new";
        }

        redirectAttributes.addFlashAttribute("success", "New admin created");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User user = adminUserService.getById(id);
        AdminUserCreateForm form = new AdminUserCreateForm();
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        model.addAttribute("userId", id);
        model.addAttribute("form", form);
        return "admin/users/edit";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable("id") long id,
            @Valid @ModelAttribute("form") AdminUserUpdateForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            return "admin/users/edit";
        }

        String oldEmail = adminUserService.getById(id).getEmail();
        String currentEmail = authentication.getName();

        try {
            adminUserService.updateAdmin(id, form);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", id);
            return "admin/users/edit";
        }

        if (oldEmail.equals(currentEmail)) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/login?relogin";
        }

        redirectAttributes.addFlashAttribute("success", "Admin updated");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/password/reset")
    public String passwordForm(@PathVariable long id, Model model) {
        model.addAttribute("userId", id);
        model.addAttribute("form", new ResetPasswordForm());
        return "admin/users/reset-password";
    }

    @PostMapping("/{id}/password/reset")
    public String changePassword(@PathVariable long id,
                                 @Valid @ModelAttribute("form") ResetPasswordForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            return "admin/users/reset-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword",
                    "password.mismatch",
                    "Пароли не совпадают");
            model.addAttribute("userId", id);
            return "admin/users/reset-password";
        }

        try {
            adminUserService.resetPassword(id, form, authentication.getName());
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", id);
            return "admin/users/reset-password";
        }

        redirectAttributes.addFlashAttribute("success", "Password reset successfully");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {

        String currentEmail = authentication.getName();

        try {
            adminUserService.deleteAdmin(id, currentEmail);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users";
        }

        redirectAttributes.addFlashAttribute("success", "Admin deleted successfully");
        return "redirect:/admin/users";
    }
}
