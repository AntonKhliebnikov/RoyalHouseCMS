package com.royalhouse.cms.admin.settings.propertybinding.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewBuildingBindingRowDto {
    private Long newBuildingId;
    private String name;
    private String address;
    private long boundPropertiesCount;
}
