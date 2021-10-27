package com.biervorrat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BierNotFoundException extends Exception {

    public BierNotFoundException(String bierName) {
        super(String.format("Bier with name %s not found in the system.", bierName));
    }

    public BierNotFoundException(Long id) {
        super(String.format("Bier with id %s not found in the system.", id));
    }
}
