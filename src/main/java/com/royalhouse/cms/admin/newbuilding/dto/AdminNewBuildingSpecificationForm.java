package com.royalhouse.cms.admin.newbuilding.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminNewBuildingSpecificationForm {

    @Valid
    private List<AdminNewBuildingSpecificationBlockForm> blocks = new ArrayList<>();
}
