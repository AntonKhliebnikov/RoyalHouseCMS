package com.royalhouse.cms.core.contact.exception;

public class ContactSettingsNotFoundException extends RuntimeException {
    public ContactSettingsNotFoundException() {
        super("Singleton record contact settings with id=1 not found. Check Flyway migration.");
    }
}