package com.biervorrat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BierStockExceededException extends Exception {
    public BierStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Biers with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}
