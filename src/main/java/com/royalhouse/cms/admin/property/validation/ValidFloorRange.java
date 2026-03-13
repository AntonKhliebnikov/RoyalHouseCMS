package com.royalhouse.cms.admin.property.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FloorNotGreaterThanTotalFloorsValidator.class)
public @interface ValidFloorRange {
    String message() default "Этаж не может быть больше общего количества этажей";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
