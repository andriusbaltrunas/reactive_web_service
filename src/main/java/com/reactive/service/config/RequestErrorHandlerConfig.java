package com.reactive.service.config;

import com.reactive.service.exception.ClientNotExistException;
import com.reactive.service.exception.ContractCreateException;
import com.reactive.service.exception.ContractNotExistException;
import com.reactive.service.exception.ValidateBodyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Configuration
public class RequestErrorHandlerConfig {

    private static final String MESSAGE = "{}";

    @Bean
    public Function<Throwable, Mono<ServerResponse>> errorHandler() {
        return throwable -> Mono.error(throwable)
                .flatMap(error -> ServerResponse.ok().build())
                .log()
                .onErrorResume(ValidateBodyException.class, validateBodyExceptionHandler)
                .onErrorResume(ContractCreateException.class, createContactException)
                .onErrorResume(ClientNotExistException.class, notExistException)
                .onErrorResume(ContractNotExistException.class, notExistException)
                .onErrorResume(Exception.class, exception);
    }

    private final Function<ValidateBodyException, Mono<ServerResponse>> validateBodyExceptionHandler =
            exception -> Mono.just(exception)
                    .doOnNext(ex -> log.info(MESSAGE, ex))
                    .flatMap(error -> ServerResponse.status(HttpStatus.BAD_REQUEST).body(BodyInserters.fromValue((exception.getValidations()))));

    private final Function<Exception, Mono<ServerResponse>> createContactException =
            exception -> Mono.just(exception)
                    .doOnNext(ex -> log.error(MESSAGE, ex.getMessage()))
                    .flatMap(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    private final Function<Exception, Mono<ServerResponse>> notExistException =
            exception -> Mono.just(exception)
                    .doOnNext(ex -> log.info(MESSAGE, ex.getMessage()))
                    .flatMap(error -> ServerResponse.status(HttpStatus.NOT_FOUND).build());

    private final Function<Exception, Mono<ServerResponse>> exception =
            exception -> Mono.just(exception)
                    .doOnNext(ex -> log.error(MESSAGE, ex.getMessage()))
                    .flatMap(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
}
