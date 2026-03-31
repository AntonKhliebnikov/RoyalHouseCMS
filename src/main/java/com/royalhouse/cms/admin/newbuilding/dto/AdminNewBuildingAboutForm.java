package com.royalhouse.cms.admin.newbuilding.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AdminNewBuildingAboutForm {
    private String currentSlide1ImagePath;
    private MultipartFile slide1Image;

    private String currentSlide2ImagePath;
    private MultipartFile slide2Image;

    private String currentSlide3ImagePath;
    private MultipartFile slide3Image;

    @Size(max = 10000, message = "Описание проекта не должно превышать 10000 символов")
    private String aboutDescription;
}
