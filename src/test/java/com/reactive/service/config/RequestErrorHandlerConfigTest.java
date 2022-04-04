package com.reactive.service.config;

import com.reactive.service.exception.ClientNotExistException;
import com.reactive.service.exception.ContractCreateException;
import com.reactive.service.exception.ValidateBodyException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestErrorHandlerConfigTest {

    private Function<Throwable, Mono<ServerResponse>> errorHandler;

    @BeforeAll
    public void setUp() {
        errorHandler = new RequestErrorHandlerConfig().errorHandler();
    }

    @Test
    void testRequestErrorHandlerConfigWhenThrowException() {
        StepVerifier.create(errorHandler.apply(new Exception()))
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void testRequestErrorHandlerConfigWhenThrowNotExistException() {
        StepVerifier.create(errorHandler.apply(new ClientNotExistException()))
                .assertNext(response -> assertEquals(HttpStatus.NOT_FOUND, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void testRequestErrorHandlerConfigWhenThrowContractCreateException() {
        StepVerifier.create(errorHandler.apply(new ContractCreateException()))
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void testRequestErrorHandlerConfigWhenThrowValidateBodyException() {
        StepVerifier.create(errorHandler.apply(new ValidateBodyException(Collections.emptyList())))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }
}