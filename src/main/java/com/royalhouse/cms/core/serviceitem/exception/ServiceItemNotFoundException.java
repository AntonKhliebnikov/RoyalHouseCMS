package com.royalhouse.cms.core.serviceitem.exception;

public class ServiceItemNotFoundException extends RuntimeException {
    public ServiceItemNotFoundException(Long id) {
        super("Service with id=" + id + " not found");
    }
}
