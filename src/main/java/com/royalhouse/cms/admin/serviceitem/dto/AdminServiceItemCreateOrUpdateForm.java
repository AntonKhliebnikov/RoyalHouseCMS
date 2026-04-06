package com.royalhouse.cms.admin.serviceitem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AdminServiceItemCreateOrUpdateForm {

    @NotBlank(message = "Название обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Описание услуги обязательно")
    @Size(max = 10000, message = "Описание услуги не должно превышать 10000 символов")
    private String description;

    private String currentBannerImagePath;
    private MultipartFile bannerImage;

    private String currentPreviewImagePAth;
    private MultipartFile previewImage;

    @NotNull(message = "Укажите отображать ли услугу")
    private Boolean isVisible;

}
