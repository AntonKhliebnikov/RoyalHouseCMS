package com.royalhouse.cms.admin.newbuilding.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminNewBuildingBasicForm {
    @NotBlank(message = "Название обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;


    private String currentBannerImagePath;
    private MultipartFile bannerImage;

    @NotNull(message = "Укажите порядок сортировки")
    @Positive(message = "Порядок сортировки должен быть больше 0")
    private Integer sortOrder;

    @NotNull(message = "Укажите статус")
    private Boolean isActive;

    private List<AdminNewBuildingInfographicItemForm> basicInfographics = new ArrayList<>();
}