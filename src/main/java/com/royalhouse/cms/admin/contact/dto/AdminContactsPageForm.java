package com.royalhouse.cms.admin.contact.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminContactsPageForm {

    @Size(max = 32, message = "Телефон не должен превышать 32 символа")
    private String phone;

    @Size(max = 32, message = "Viber не должен превышать 32 символа")
    private String viberPhone;

    @Size(max = 64, message = "Telegram не должен превышать 64 символа")
    private String telegramUsername;

    @Email(message = "Введите корректный email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String email;

    @Size(max = 255, message = "Instagram не должен превышать 255 символов")
    private String instagramUrl;

    @Size(max = 255, message = "Facebook не должен превышать 255 символов")
    private String facebookUrl;

    @Size(max = 255, message = "Адрес не должен превышать 255 символов")
    private String address;

    @Valid
    private List<AdminRecipientEmailItemForm> recipientEmails = new ArrayList<>();
}
