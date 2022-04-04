package com.reactive.service.router;

import com.reactive.service.router.handler.ContractRouterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ContractRouter {

    private static final String ROOT_ROUTE = "/clients/{id}/contracts";

    @Bean
    public RouterFunction<ServerResponse> saveContract(ContractRouterHandler contractRouterHandler) {
        return route(GET(ROOT_ROUTE), contractRouterHandler::getContracts)
                .andRoute(GET(ROOT_ROUTE +"/{contractId}"), contractRouterHandler::getContractById)
                .andRoute(GET(ROOT_ROUTE + "/latest/contract"), contractRouterHandler::getLatestContract)
                .andRoute(POST(ROOT_ROUTE + "/applicant/contract").and(accept(MediaType.APPLICATION_JSON)), contractRouterHandler::saveContactApplicant)
                .andRoute(PATCH(ROOT_ROUTE + "/{contractId}/buyer").and(accept(MediaType.APPLICATION_JSON)), contractRouterHandler::saveContractBayerOrSeller);
    }
}