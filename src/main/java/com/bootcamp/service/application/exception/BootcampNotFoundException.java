package com.bootcamp.service.application.exception;

public class BootcampNotFoundException extends RuntimeException {
    public BootcampNotFoundException(String message) {
        super(message);
    }
}
