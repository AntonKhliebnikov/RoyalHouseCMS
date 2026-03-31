package com.royalhouse.cms.admin.newbuilding.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminNewBuildingLocationForm {
    @NotBlank(message = "Укажите город")
    @Size(max = 120, message = "Название города не должно превышать 120 символов")
    private String city;

    @NotBlank(message = "Укажите район")
    @Size(max = 120, message = "Название района не должно превышать 120 символов")
    private String district;

    @NotBlank(message = "Укажите улицу")
    @Size(max = 150, message = "Название улицы не должно превышать 150 символов")
    private String street;

    @NotBlank(message = "Укажите номер дома")
    @Size(max = 30, message = "Номер дома не должен превышать 30 символов")
    private String houseNumber;

    @NotNull(message = "Укажите широту")
    @Digits(integer = 3, fraction = 6, message = "Широта должна быть в формате до 3 цифр до запятой и 6 после")
    @DecimalMin(value = "-90.000000", message = "Широта не может быть меньше -90")
    @DecimalMax(value = "90.000000", message = "Широта не может быть больше 90")
    private BigDecimal latitude;

    @NotNull(message = "Укажите долготу")
    @Digits(integer = 3, fraction = 6, message = "Долгота должна быть в формате до 3 цифр до запятой и 6 после")
    @DecimalMin(value = "-180.000000", message = "Долгота не может быть меньше -180")
    @DecimalMax(value = "180.000000", message = "Долгота не может быть больше 180")
    private BigDecimal longitude;

    @Size(max = 10000, message = "Описание местоположения не должно превышать 10000 символов")
    private String locationDescription;
}
