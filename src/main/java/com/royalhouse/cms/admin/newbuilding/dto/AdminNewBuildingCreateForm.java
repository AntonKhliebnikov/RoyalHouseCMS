package com.royalhouse.cms.admin.newbuilding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNewBuildingCreateForm {

    @NotBlank(message = "Название обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @NotNull(message = "Укажите порядок сортировки")
    @Positive(message = "Порядок сортировки должен быть больше 0")
    private Integer sortOrder;

    @NotNull(message = "Укажите статус")
    private Boolean isActive = true;
}
