package com.royalhouse.cms.admin.newbuilding.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AdminNewBuildingPanoramaForm {
    private String currentPanoramaImagePath;
    private MultipartFile panoramaImage;
}
