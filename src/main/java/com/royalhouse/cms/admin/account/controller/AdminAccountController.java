package com.royalhouse.cms.admin.account.controller;

import com.royalhouse.cms.admin.user.dto.ChangePasswordForm;
import com.royalhouse.cms.admin.user.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/account")
public class AdminAccountController {
    private final AdminUserService adminUserService;

    public AdminAccountController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/password")
    public String changePasswordForm(Model model) {
        model.addAttribute("form", new ChangePasswordForm());
        return "admin/account/password";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("form") ChangePasswordForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 Authentication authentication,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "admin/account/password";
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword",
                    "password.mismatch",
                    "Пароли не совпадают");
            return "admin/account/password";
        }

        try {
            adminUserService.changeOwnPassword(authentication.getName(), form);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/account/password";
        }

        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/login?relogin";
    }
}