package com.royalhouse.cms.admin.settings.propertybinding.dto;

import com.royalhouse.cms.core.property.entity.PropertyType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CandidatePropertyRowDto {
    private Long propertyId;
    private PropertyType propertyType;
    private BigDecimal area;
    private BigDecimal price;
    private Integer rooms;
    private Integer floor;
    private Integer totalFloors;
    private String address;
}
