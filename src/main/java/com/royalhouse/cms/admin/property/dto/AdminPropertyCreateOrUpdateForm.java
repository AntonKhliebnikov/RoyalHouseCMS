package com.royalhouse.cms.admin.property.dto;

import com.royalhouse.cms.admin.property.validation.ValidFloorRange;
import com.royalhouse.cms.core.property.entity.PropertyType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@ValidFloorRange
@Getter
@Setter
public class AdminPropertyCreateOrUpdateForm {
    @NotNull
    private PropertyType propertyType;

    @NotNull
    @Positive
    @Digits(integer = 8, fraction = 2)
    private BigDecimal area;

    @NotNull
    @Positive
    @Digits(integer = 13, fraction = 2)
    private BigDecimal price;

    @PositiveOrZero
    private Integer rooms;

    @PositiveOrZero
    private Integer floor;

    @PositiveOrZero
    private Integer totalFloors;

    @NotBlank(message = "Укажите город")
    @Size(max = 120, message = "Город не должен быть длиннее 120 символов")
    private String city;

    @NotBlank(message = "Укажите район")
    @Size(max = 120, message = "Район не должен быть длиннее 120 символов")
    private String district;

    @NotBlank(message = "Укажите улицу")
    @Size(max = 150, message = "Улица не должна быть длиннее 150 символов")
    private String street;

    @NotBlank(message = "Укажите номер дома")
    @Size(max = 30, message = "Номер дома не должен быть длиннее 30 символов")
    private String houseNumber;
}