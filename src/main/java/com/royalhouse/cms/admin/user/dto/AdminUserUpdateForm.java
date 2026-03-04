package com.royalhouse.cms.admin.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserUpdateForm {

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
}