package com.royalhouse.cms.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationForm {

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(min = 8, max = 50)
    private String password;

    @NotBlank
    private String confirmPassword;
}
