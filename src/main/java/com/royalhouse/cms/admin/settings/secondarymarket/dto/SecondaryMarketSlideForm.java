package com.royalhouse.cms.admin.settings.secondarymarket.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SecondaryMarketSlideForm {
    private Long id;
    private MultipartFile image;
    private String imagePath;
    private String text;
    private String linkUrl;
    private Integer sortOrder;
    private Boolean isActive = true;
    private Boolean markedForDelete = false;
}
