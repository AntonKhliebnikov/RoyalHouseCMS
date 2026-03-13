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
}