package com.royalhouse.cms.core.property.exception;

public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException(Long id) {
        super("Property with id " + id + " not found");
    }
}