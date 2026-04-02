package com.royalhouse.cms.admin.newbuilding.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNewBuildingSpecificationBlockForm {
    private Integer sortOrder;

    @Size(max = 20000, message = "Содержимое блока не должно превышать 20000 символов")
    private String content;
}
