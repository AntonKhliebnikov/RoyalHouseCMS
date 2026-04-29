package com.royalhouse.cms.admin.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRecipientEmailItemForm {

    private Long id;

    @Email(message = "Введите корректный email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String email;

    private Boolean isActive;

    private Boolean markedForDelete = false;
}
