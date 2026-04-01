package com.royalhouse.cms.admin.newbuilding.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AdminNewBuildingInfographicItemForm {
    private Integer sortOrder;
    private String description;
    private String currentImagePath;
    private MultipartFile image;
}