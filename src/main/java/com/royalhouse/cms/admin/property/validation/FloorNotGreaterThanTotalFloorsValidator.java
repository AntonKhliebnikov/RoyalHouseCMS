package com.royalhouse.cms.admin.property.validation;

import com.royalhouse.cms.admin.property.dto.AdminPropertyCreateOrUpdateForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FloorNotGreaterThanTotalFloorsValidator
        implements ConstraintValidator<ValidFloorRange, AdminPropertyCreateOrUpdateForm> {
    @Override
    public boolean isValid(AdminPropertyCreateOrUpdateForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        Integer floor = form.getFloor();
        Integer totalFloors = form.getTotalFloors();

        if (floor == null || totalFloors == null) {
            return true;
        }

        if (floor <= totalFloors) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("floor")
                .addConstraintViolation();

        return false;
    }
}