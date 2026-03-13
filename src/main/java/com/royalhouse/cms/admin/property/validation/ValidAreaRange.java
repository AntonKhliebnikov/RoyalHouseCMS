package com.royalhouse.cms.admin.property.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AreaFromNotGreaterThanAreaTo.class)
public @interface ValidAreaRange {
    String message() default "Площадь \"От\" не может быть больше площади \"До\"";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
