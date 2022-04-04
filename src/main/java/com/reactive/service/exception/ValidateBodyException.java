package com.reactive.service.exception;

import lombok.Getter;
import com.reactive.service.model.api.exception.Validation;

import java.util.List;

@Getter
public class ValidateBodyException extends RuntimeException {
    private final List<Validation> validations;

    public ValidateBodyException(List<Validation> validations) {
        this.validations = validations;
    }
}
