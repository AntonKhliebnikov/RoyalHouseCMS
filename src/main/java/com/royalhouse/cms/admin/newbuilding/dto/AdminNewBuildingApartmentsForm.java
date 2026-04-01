package com.royalhouse.cms.admin.newbuilding.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminNewBuildingApartmentsForm {
    private String currentSlide1ImagePath;
    private MultipartFile slide1Image;

    private String currentSlide2ImagePath;
    private MultipartFile slide2Image;

    private String currentSlide3ImagePath;
    private MultipartFile slide3Image;

    @Size(max = 10000, message = "Описание квартир не должно превышать 10000 символов")
    private String apartmentsDescription;

    private List<AdminNewBuildingInfographicItemForm> apartmentsInfographic = new ArrayList<>();
}
