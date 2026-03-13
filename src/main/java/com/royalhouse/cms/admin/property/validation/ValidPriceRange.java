package com.royalhouse.cms.admin.property.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceFromNotGreaterThanPriceTo.class)
public @interface ValidPriceRange {
    String message() default "Цена \"От\" не может быть больше цены \"До\"";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
