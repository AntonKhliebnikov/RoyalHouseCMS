package com.royalhouse.cms.admin.common;

import com.royalhouse.cms.core.property.exception.PropertyNotFoundException;
import com.royalhouse.cms.core.user.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "com.royalhouse.cms.admin")
public class AdminControllerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/admin/users";
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public String handlePropertyNotFound(PropertyNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/admin/properties";
    }
}