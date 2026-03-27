package com.royalhouse.cms.admin.property.dto;

import com.royalhouse.cms.admin.property.validation.ValidAreaRange;
import com.royalhouse.cms.admin.property.validation.ValidPriceRange;
import com.royalhouse.cms.core.property.entity.PropertyType;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@ValidPriceRange
@ValidAreaRange
@Getter
@Setter
public class AdminPropertyFilterForm {
    private Long id;
    private PropertyType propertyType;

    @Positive
    private BigDecimal areaFrom;

    @Positive
    private BigDecimal areaTo;

    @Positive
    private BigDecimal priceFrom;

    @Positive
    private BigDecimal priceTo;

    @Positive
    private Integer rooms;
}
