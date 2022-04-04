package com.reactive.service.router.handler;

import com.reactive.service.validator.RequestBodyValidator;
import com.reactive.service.model.api.ContractApplicant;
import com.reactive.service.model.api.ContractBuyerSellerInformation;
import com.reactive.service.model.dto.Contract;
import com.reactive.service.service.ContractService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class ContractRouterHandler extends AbstractRouterHandler<Contract> {

    private static final String ID = "id";
    private static final String CONTRACT_ID = "contractId";

    private final ContractService contractService;

    public ContractRouterHandler(RequestBodyValidator requestBodyValidator,
                                 Function<Throwable, Mono<ServerResponse>> errorHandler,
                                 ContractService contractService) {
        super(requestBodyValidator, errorHandler, Contract.class);
        this.contractService = contractService;
    }

    public Mono<ServerResponse> getContracts(ServerRequest serverRequest) {
        return fluxResponse().apply(Flux.just(serverRequest.pathVariable(ID))
                        .flatMap(contractService.getContracts()))
                .switchIfEmpty(notFoundResponse())
                .onErrorResume(errorHandler);
    }

    public Mono<ServerResponse> getContractById(ServerRequest serverRequest) {
        return contractService.getContractById(serverRequest.pathVariable(ID), serverRequest.pathVariable(CONTRACT_ID))
                .flatMap(monoResponse())
                .onErrorResume(errorHandler);
    }

    public Mono<ServerResponse> saveContactApplicant(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(ContractApplicant.class)
                .flatMap(requestBodyValidator.validate())
                .flatMap(body -> contractService.saveContractApplicant(serverRequest.pathVariable(ID), body))
                .flatMap(monoResponse())
                .onErrorResume(errorHandler);
    }

    public Mono<ServerResponse> saveContractBayerOrSeller(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(ContractBuyerSellerInformation.class)
                .flatMap(requestBodyValidator.validate())
                .flatMap(body -> contractService.saveContractBuyerOrSeller(serverRequest.pathVariable(ID), serverRequest.pathVariable(CONTRACT_ID), body))
                .flatMap(monoResponse())
                .onErrorResume(errorHandler);
    }

    public Mono<ServerResponse> getLatestContract(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.pathVariable(ID))
                .flatMap(contractService.getLatestContract())
                .flatMap(monoResponse())
                .onErrorResume(errorHandler);
    }
}