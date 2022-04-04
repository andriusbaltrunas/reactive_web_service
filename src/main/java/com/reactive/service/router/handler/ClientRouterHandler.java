package com.reactive.service.router.handler;

import com.reactive.service.model.Client;
import com.reactive.service.validator.RequestBodyValidator;
import com.reactive.service.model.api.ClientRequestData;
import com.reactive.service.service.ClientService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class ClientRouterHandler extends AbstractRouterHandler<Client> {

    private static final String ID = "id";

    private final ClientService clientService;

    public ClientRouterHandler(RequestBodyValidator requestBodyValidator,
                               Function<Throwable, Mono<ServerResponse>> errorHandler,
                               ClientService clientService) {
        super(requestBodyValidator, errorHandler, Client.class);
        this.clientService = clientService;
    }

    public Mono<ServerResponse> createClient(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(ClientRequestData.class)
                .flatMap(requestBodyValidator.validate())
                .flatMap(clientService.create())
                .flatMap(monoResponse())
                .onErrorResume(errorHandler);
    }

    public Mono<ServerResponse> getClients(ServerRequest serverRequest) {
        return Mono.justOrEmpty(clientService.getClients())
                .flatMap(fluxResponse())
                .onErrorResume(errorHandler);
    }

    public Mono<ServerResponse> getClientById(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.pathVariable(ID))
                .flatMap(clientService.findById())
                .flatMap(monoResponse())
                .switchIfEmpty(notFoundResponse());
    }

    public Mono<ServerResponse> getClientByPhone(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(ClientRequestData.class)
                .flatMap(requestBodyValidator.validate())
                .flatMap(clientService.getClientByPhone())
                .flatMap(monoResponse())
                .switchIfEmpty(notFoundResponse());
    }
}
