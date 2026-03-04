package com.royalhouse.cms.admin.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordForm {

    @NotBlank
    @Size(min = 8, max = 50)
    private String newPassword;

    @NotBlank
    @Size(min = 8, max = 50)
    private String confirmNewPassword;
}
