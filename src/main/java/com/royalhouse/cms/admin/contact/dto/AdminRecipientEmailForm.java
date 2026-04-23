package com.royalhouse.cms.admin.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRecipientEmailForm {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String email;
}
