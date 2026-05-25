package com.royalhouse.cms.admin.settings.secondarymarket.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SecondaryMarketSlideForm {
    private Long id;
    private MultipartFile image;
    private String imagePath;

    @Size(max = 255, message = "Текст баннера не должен превышать 255 символов")
    private String text;

    @Size(max = 500, message = "Ссылка не должна превышать 500 символов")
    private String linkUrl;
    private Integer sortOrder;
    private Boolean isActive = true;
    private Boolean markedForDelete = false;
}
