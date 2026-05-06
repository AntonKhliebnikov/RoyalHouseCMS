package com.royalhouse.cms.admin.settings.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRecipientEmailItemForm {

    private Long id;

    @Email(message = "Введите корректный email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    @NotBlank(message = "Поле Email получателя не должно быть пустым")
    private String email;

    private Boolean isActive;

    private Boolean markedForDelete = false;
}
