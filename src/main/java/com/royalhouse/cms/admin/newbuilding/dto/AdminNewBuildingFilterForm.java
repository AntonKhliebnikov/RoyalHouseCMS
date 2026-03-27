package com.royalhouse.cms.admin.newbuilding.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNewBuildingFilterForm {
    private String name;
    private String address;
    private Boolean isActive;
}
