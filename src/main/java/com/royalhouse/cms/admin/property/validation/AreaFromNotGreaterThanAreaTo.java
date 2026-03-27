package com.royalhouse.cms.admin.property.validation;

import com.royalhouse.cms.admin.property.dto.AdminPropertyFilterForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class AreaFromNotGreaterThanAreaTo
        implements ConstraintValidator<ValidAreaRange, AdminPropertyFilterForm> {
    @Override
    public boolean isValid(AdminPropertyFilterForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        BigDecimal areaFrom = form.getAreaFrom();
        BigDecimal areaTo = form.getAreaTo();

        if (areaFrom == null || areaTo == null) {
            return true;
        }

        if (areaFrom.compareTo(areaTo) <= 0) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("areaFrom")
                .addConstraintViolation();
        return false;
    }
}
