package com.royalhouse.cms.core.common.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Address {

    @NotNull
    @Column(name = "city", length = 120)
    private String city;

    @NotNull
    @Column(name = "district", length = 120)
    private String district;

    @NotNull
    @Column(name = "street", length = 150)
    private String street;

    @NotNull
    @Column(name = "house_number", length = 30)
    private String houseNumber;
}
