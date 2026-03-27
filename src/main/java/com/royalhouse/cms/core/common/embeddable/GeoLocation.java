package com.royalhouse.cms.core.common.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class GeoLocation {

    @NotNull
    @Digits(integer = 3, fraction = 6)
    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 3, fraction = 6)
    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;
}