package com.royalhouse.cms.admin.property.validation;

import com.royalhouse.cms.admin.property.dto.AdminPropertyFilterForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class PriceFromNotGreaterThanPriceTo
        implements ConstraintValidator<ValidPriceRange, AdminPropertyFilterForm> {
    @Override
    public boolean isValid(AdminPropertyFilterForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        BigDecimal priceFrom = form.getPriceFrom();
        BigDecimal priceTo = form.getPriceTo();

        if (priceFrom == null || priceTo == null) {
            return true;
        }

        if (priceFrom.compareTo(priceTo) <= 0) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("priceFrom")
                .addConstraintViolation();

        return false;
    }
}
