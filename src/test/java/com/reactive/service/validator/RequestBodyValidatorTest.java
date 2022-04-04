package com.reactive.service.validator;

import com.reactive.service.exception.ValidateBodyException;
import com.reactive.service.model.api.exception.Validation;
import com.reactive.service.model.dto.Contract;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestBodyValidatorTest {

    private static final String NAME = "test_name";
    private static final String MESSAGE = "test_message";

    @InjectMocks
    private RequestBodyValidator requestBodyValidator;
    @Mock
    private Validator validator;
    @Mock
    private Contract contract;
    @Mock
    private ConstraintViolation<Contract> constraintViolation;

    @Test
    void testValidateObjectWhenSomeFieldsMissing() {
        PathImpl path = PathImpl.createRootPath();
        path.addContainerElementNode(NAME);

        when(validator.validate(any(Contract.class))).thenReturn(Set.of(constraintViolation));
        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(constraintViolation.getMessage()).thenReturn(MESSAGE);

        StepVerifier.create(requestBodyValidator.validate().apply(contract))
                .expectErrorMatches(error -> {
                    if (!(error instanceof ValidateBodyException ex)) {
                        return false;
                    }
                    Validation validation = ex.getValidations().get(0);
                    return validation.getField().equals(NAME) && validation.getMessage().equals(MESSAGE);
                })
                .verify();
    }

    @Test
    void testValidateObjectWhenAllFieldsFilled() {
        when(validator.validate(any(Contract.class))).thenReturn(Collections.emptySet());
        StepVerifier.create(requestBodyValidator.validate().apply(contract))
                .assertNext(contract -> assertAll(
                        () -> assertNotNull(contract),
                        () -> assertInstanceOf(Contract.class, contract)
                ))
                .verifyComplete();
    }
}