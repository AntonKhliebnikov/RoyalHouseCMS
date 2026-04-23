package com.royalhouse.cms.core.application.exception;

public class ApplicationRecipientEmailNotFoundException extends RuntimeException {
    public ApplicationRecipientEmailNotFoundException(String message) {
        super(message);
    }
}