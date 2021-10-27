package com.biervorrat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BierAlreadyRegisteredException extends Exception {
    public BierAlreadyRegisteredException(String name) {
        super(String.format("Beer with name %s was already registered in the system.", name));
    }
}
