package com.royalhouse.cms.core.property.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class PropertyUnitDetails {

    @Column(name = "building_section", length = 50)
    private String buildingSection;

    @Column(name = "unit_number",  length = 30)
    private String unitNumber;
}
