package com.royalhouse.cms.core.newbuilding.exception;

public class NewBuildingNotFoundException extends RuntimeException {
    public NewBuildingNotFoundException(Long id) {
        super("New building with id=" + id + " not found");
    }
}