package com.reactive.service.validator;

import com.reactive.service.exception.ValidateBodyException;
import com.reactive.service.model.api.exception.Validation;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class RequestBodyValidator {

    private final Validator validator;

    public RequestBodyValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> Function<T, Mono<T>> validate() {
        return body -> {
            Set<ConstraintViolation<T>> violations = validator.validate(body);
            return violations.isEmpty() ? Mono.just(body) : Mono.error(new ValidateBodyException(buildValidationErrors(violations)));
        };
    }

    private <T> List<Validation> buildValidationErrors(Set<ConstraintViolation<T>> violations) {
        List<Validation> errors = new ArrayList<>();

        violations.forEach(v -> errors.add(Validation.builder()
                .field(((PathImpl) v.getPropertyPath()).getLeafNode().getName())
                .message(v.getMessage())
                .build()));

        return errors;
    }
}