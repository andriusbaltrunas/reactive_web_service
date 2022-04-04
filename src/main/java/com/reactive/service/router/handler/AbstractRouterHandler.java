package com.reactive.service.router.handler;

import com.reactive.service.validator.RequestBodyValidator;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public abstract class AbstractRouterHandler<T> {

    protected final RequestBodyValidator requestBodyValidator;
    protected final Function<Throwable, Mono<ServerResponse>> errorHandler;
    private final Class<T> handlerClass;

    public AbstractRouterHandler(RequestBodyValidator requestBodyValidator, Function<Throwable, Mono<ServerResponse>> errorHandler, Class<T> handlerClass) {
        this.requestBodyValidator = requestBodyValidator;
        this.errorHandler = errorHandler;
        this.handlerClass = handlerClass;
    }

    protected <R> Function<R, Mono<ServerResponse>> monoResponse() {
        return body -> ServerResponse.ok().body(Mono.just(body), handlerClass);
    }

    protected <R> Function<R, Mono<ServerResponse>> fluxResponse() {
        return body -> ServerResponse.ok().body(body, handlerClass);
    }

    protected Mono<ServerResponse> notFoundResponse() {
        return ServerResponse.notFound().build();
    }
}
