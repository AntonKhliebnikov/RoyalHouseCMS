package com.royalhouse.cms.admin.settings.aboutcompany.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AboutCompanySettingsForm {
    private MultipartFile bannerImage;
    private String bannerImagePath;

    @Size(max = 255, message = "Текст на баннере не должен превышать 255 символов")
    private String bannerText;

    @NotBlank(message = "Заголовок обязателен")
    @Size(max = 255, message = "Заголовок не должен превышать 255 символов")
    private String title;

    @Size(max = 65535, message = "Описание слишком длинное")
    private String description;
}