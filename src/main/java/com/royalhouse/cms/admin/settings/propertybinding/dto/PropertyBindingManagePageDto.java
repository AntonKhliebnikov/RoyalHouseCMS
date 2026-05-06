package com.royalhouse.cms.admin.settings.propertybinding.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PropertyBindingManagePageDto {
    private Long newBuildingId;
    private String newBuildingName;
    private String newBuildingAddress;
    private List<BoundPropertyRowDto> boundProperties;
    private List<CandidatePropertyRowDto> candidateProperties;
}
